package ganachoco.wear.clockface.cairoclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.Gravity;
import android.view.SurfaceHolder;

import java.util.TimeZone;

public class BaseWatchFaceService extends CanvasWatchFaceService {

    private Matrix mMatrix = new Matrix();
    private Paint mPaint = new Paint();
    private TimeZone mTZ;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {
                String tz = intent.getStringExtra("time-zone");
                mTZ = TimeZone.getTimeZone(tz);
            }
        }
    };

    private interface TimeSource {
        long getTime();
    }

    private class ExactTimeSource implements TimeSource {
        public long getTime() {
            long time = System.currentTimeMillis();
            return time + mTZ.getOffset(time);
        }
    }

    private class RoughTimeSource extends ExactTimeSource {
        private final int mResolution;
        public RoughTimeSource(int resolution) {
            mResolution = resolution;
        }

        @Override
        public long getTime() {
            return super.getTime() / mResolution * mResolution;
        }
    }

    private class FakeTimeSource implements TimeSource {
        private final long mTime;
        public FakeTimeSource(long time) {
            mTime = time;
        }
        public long getTime() {
            return mTime;
        }
    }

    private TimeSource mTimeSource;

    @Override
    public void onCreate() {
        super.onCreate();
        mTZ = TimeZone.getDefault();
        mTimeSource = new ExactTimeSource();
        mPaint.setARGB(255, 255, 255, 255);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        if (mBackgroundBitmap != null) mBackgroundBitmap.recycle();
        if (mForegroundBitmap != null) mForegroundBitmap.recycle();
        for (Bitmap b: mBitmaps) {
            if (b != null) b.recycle();
        }
        super.onDestroy();
    }

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static final long SEC_BASE = 1000 * 60;
    private static final long MIN_BASE = 1000 * 60 * 60;
    private static final long HOUR_BASE = 1000 * 60 * 60 * 12;

    private void getHandAngles(float[] outAngles) {
        final long time = mTimeSource.getTime();
        outAngles[0] = (time % HOUR_BASE) * 360 / (float) HOUR_BASE;
        outAngles[1] = (time % MIN_BASE) * 360 / (float) MIN_BASE;
        outAngles[2] = (time % SEC_BASE) *360 / (float) SEC_BASE;
    }

    private Bitmap mBackgroundBitmap, mForegroundBitmap;
    private Bitmap[] mBitmaps;
    private Rect mHandClip;
    private float mShadowOffset;
    private int mHalfBitmapWidth;
    private float mScale = 1.0f;

    static final int ID_HOUR_HAND_SHADOW = 0;
    static final int ID_MINUTE_HAND_SHADOW = 1;
    static final int ID_SECOND_HAND_SHADOW = 2;
    static final int ID_HOUR_HAND = 3;
    static final int ID_MINUTE_HAND = 4;
    static final int ID_SECOND_HAND = 5;
    static final int ID_DROP_SHADOW = 6;
    static final int ID_FACE = 7;
    static final int ID_MARKS = 8;
    static final int ID_FACE_SHADOW = 9;
    static final int ID_GLASS = 10;
    static final int ID_FRAME = 11;
    static final int ID_MAX = 12;

    protected void setupWatchStyle(Engine engine) {

    }

    private Bitmap composite(int [] resIds) {
        Bitmap b1 = BitmapFactory.decodeResource(getResources(), resIds[0]);
        Bitmap bitmap = Bitmap.createBitmap(b1.getWidth(),
                b1.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(b1, mMatrix, null);
        b1.recycle();
        for (int i = 1; i < resIds.length; i++) {
            Bitmap b2 = BitmapFactory.decodeResource(getResources(), resIds[i]);
            canvas.drawBitmap(b2, mMatrix, null);
            b2.recycle();
        }
        return bitmap;
    }

    protected void loadBitmaps(int[] resIds) {
        final int[] foreIds = {
                resIds[ID_FACE_SHADOW],
                resIds[ID_GLASS],
                resIds[ID_FRAME],
        };
        mForegroundBitmap = composite(foreIds);

        final int[] backIds = {
                resIds[ID_DROP_SHADOW],
                resIds[ID_FACE],
                resIds[ID_MARKS],
        };
        mBackgroundBitmap = composite(backIds);

        mBitmaps = new Bitmap[6];
        for (int i = 0; i < mBitmaps.length; i++) {
            mBitmaps[i] = BitmapFactory.decodeResource(getResources(),
                    resIds[ID_HOUR_HAND_SHADOW + i]);
        }

        final int width = mBackgroundBitmap.getWidth();
        mHalfBitmapWidth = width / 2;

        final float clipOffset = 0.1f;
        mHandClip = new Rect(
                (int)(mHalfBitmapWidth * (1.0f - clipOffset)),
                (int)(mHalfBitmapWidth * (1.0f - clipOffset)),
                width,
                (int)(mHalfBitmapWidth * (1.0f + clipOffset)));

        mShadowOffset = width / 80.0f;
    }

    private void drawHand(Canvas canvas, Bitmap bitmap, float angle) {
        canvas.save();
        canvas.rotate(angle, mHalfBitmapWidth, mHalfBitmapWidth);
        canvas.drawBitmap(bitmap, mHandClip, mHandClip, null);
        canvas.restore();
    }

    private void onDrawClockFace(Canvas canvas, boolean isAmbientMode) {
        float[] angles = new float[3];
        getHandAngles(angles);

        final float hourAngle = angles[0] - 90f;
        final float minAngle = angles[1] - 90f;
        final float secAngle = angles[2] - 90f;

        {
            canvas.save();
            canvas.scale(mScale, mScale);
            canvas.drawBitmap(mBackgroundBitmap, mMatrix, mPaint);
            {
                canvas.save();
                canvas.translate(mShadowOffset, mShadowOffset);

                drawHand(canvas, mBitmaps[ID_HOUR_HAND_SHADOW], hourAngle);
                drawHand(canvas, mBitmaps[ID_MINUTE_HAND_SHADOW], minAngle);

                if (!isAmbientMode) {
                    drawHand(canvas, mBitmaps[ID_SECOND_HAND_SHADOW], secAngle);
                }
                canvas.restore();
            }

            drawHand(canvas, mBitmaps[ID_HOUR_HAND], hourAngle);
            drawHand(canvas, mBitmaps[ID_MINUTE_HAND], minAngle);

            if (!isAmbientMode) {
                drawHand(canvas, mBitmaps[ID_SECOND_HAND], secAngle);
            }

            canvas.drawBitmap(mForegroundBitmap, mMatrix, null);
            canvas.restore();
        }
    }

    class Engine extends CanvasWatchFaceService.Engine {
        private static final int MSG_UPDATE_TIME_CONTINUOUSLY = 0;
        private Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case MSG_UPDATE_TIME_CONTINUOUSLY:
                        if (isVisible() && !isInAmbientMode()) {
                            invalidate();
                            sendEmptyMessageDelayed(MSG_UPDATE_TIME_CONTINUOUSLY, 100);
                        }
                        break;
                }
            }
        };

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);
            setupWatchStyle(this);
            setWatchFaceStyle(new WatchFaceStyle.Builder(BaseWatchFaceService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setBackgroundVisibility(WatchFaceStyle
                            .BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setAmbientPeekMode(WatchFaceStyle.AMBIENT_PEEK_MODE_HIDDEN)
                    .setHotwordIndicatorGravity(Gravity.TOP | Gravity.RIGHT)
                    .setStatusBarGravity(Gravity.TOP | Gravity.LEFT)
                    .setShowUnreadCountIndicator(true)
                    .setViewProtection(WatchFaceStyle.PROTECT_HOTWORD_INDICATOR |
                            WatchFaceStyle.PROTECT_STATUS_BAR)
                    .setShowSystemUiTime(false)
                    .build());
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            mScale = (float) width / (float) (mHalfBitmapWidth * 2);
            mHandler.sendEmptyMessage(MSG_UPDATE_TIME_CONTINUOUSLY);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean isAmbientMode) {
            super.onAmbientModeChanged(isAmbientMode);
            if (isAmbientMode) {
                final int SECOND_ON_MILLIS = 1000;
                mTimeSource = new RoughTimeSource(SECOND_ON_MILLIS);
                invalidate();
            } else {
                mTimeSource = new ExactTimeSource();
                mHandler.sendEmptyMessage(MSG_UPDATE_TIME_CONTINUOUSLY);
            }
        }

        @Override
        public void onDraw(Canvas canvas, Rect bound) {
            onDrawClockFace(canvas, isInAmbientMode());
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                mHandler.sendEmptyMessage(MSG_UPDATE_TIME_CONTINUOUSLY);
            }
        }
    }
}