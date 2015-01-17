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
import android.view.SurfaceHolder;

import java.util.TimeZone;

public class BaseWatchFaceService extends CanvasWatchFaceService {
    @Override
    public Engine onCreateEngine() {
        mTZ = TimeZone.getDefault();
        mTimeSource = new RoughTimeSource();
        mPaint.setARGB(255, 255, 255, 255);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        return new Engine();
    }

    private Matrix mMatrix = new Matrix();
    private Paint mPaint = new Paint();

    private TimeZone mTZ;
    private boolean mRegisteredReceiver;
    private void registerReceiver() {
        if (mRegisteredReceiver) {
            return;
        }

        IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
        registerReceiver(mReceiver, filter);
        mRegisteredReceiver = true;
    }

    private void unregisterReceiver() {
        if (!mRegisteredReceiver) {
            return;
        }
        unregisterReceiver(mReceiver);
        mRegisteredReceiver = false;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {
                String tz = intent.getStringExtra("time-zone");
                mTZ = TimeZone.getTimeZone(tz);
            }
        }
    };


    private static final long SEC_BASE = 1000 * 60;
    private static final long MIN_BASE = 1000 * 60 * 60;
    private static final long HOUR_BASE = 1000 * 60 * 60 * 12;

    private interface TimeSource {
        long getTime();
    }

    private class ExactTimeSource implements TimeSource {
        public long getTime() {
            long time = System.currentTimeMillis();
            return time + mTZ.getOffset(time);
        }
    }

    private class RoughTimeSource implements  TimeSource {
        public long getTime() {
            long time = System.currentTimeMillis() / 1000 * 1000;
            return time + mTZ.getOffset(time);
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

    private Bitmap mBackgroundBitmap, mForegroundBitmap;
    private Bitmap[] mBitmaps;

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
        int[] foreIds = {
                resIds[ID_FACE_SHADOW],
                resIds[ID_GLASS],
                resIds[ID_FRAME],
        };
        mForegroundBitmap = composite(foreIds);

        int[] backIds = {
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
    }

    private void onDrawClockFace(Canvas canvas, Rect bound,
                                   float hourAngle, float minAngle, float secAngle,
                                   boolean isAmbientMode) {

        float scale = 1.0f;
        final int width = mBitmaps[ID_HOUR_HAND].getWidth();

        final float shadowOffset = width / 80.0f;
        final float halfWidth = width / 2.0f;
        if (width != bound.width()) {
            scale = (float) bound.width() / (float) width;
        }

        final Rect handClip = new Rect((int)(halfWidth * 0.8f), (int)(halfWidth * 0.8f),
                width, (int)(halfWidth * 1.2f));

        {
            canvas.save();
            canvas.scale(scale, scale);
            canvas.drawBitmap(mBackgroundBitmap, mMatrix, mPaint);
            {
                canvas.save();
                canvas.translate(shadowOffset, shadowOffset);
                {
                    canvas.save();
                    canvas.rotate(hourAngle, halfWidth, halfWidth);
                    canvas.drawBitmap(mBitmaps[ID_HOUR_HAND_SHADOW], handClip, handClip, null);
                    canvas.restore();
                }
                {
                    canvas.save();
                    canvas.rotate(minAngle, halfWidth, halfWidth);
                    canvas.drawBitmap(mBitmaps[ID_MINUTE_HAND_SHADOW], handClip, handClip, null);
                    canvas.restore();
                }
                if (!isAmbientMode) {
                    canvas.save();
                    canvas.rotate(secAngle, halfWidth, halfWidth);
                    canvas.drawBitmap(mBitmaps[ID_SECOND_HAND_SHADOW], handClip, handClip, null);
                    canvas.restore();
                }
                canvas.restore();
            }
            {
                canvas.save();
                canvas.rotate(hourAngle, halfWidth, halfWidth);
                canvas.drawBitmap(mBitmaps[ID_HOUR_HAND], handClip, handClip, null);
                canvas.restore();
            }
            {
                canvas.save();
                canvas.rotate(minAngle, halfWidth, halfWidth);
                canvas.drawBitmap(mBitmaps[ID_MINUTE_HAND], handClip, handClip, null);
                canvas.restore();
            }
            if (!isAmbientMode) {
                canvas.save();
                canvas.rotate(secAngle, halfWidth, halfWidth);
                canvas.drawBitmap(mBitmaps[ID_SECOND_HAND], handClip, handClip, null);
                canvas.restore();
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
            mHandler.sendEmptyMessage(MSG_UPDATE_TIME_CONTINUOUSLY);
            registerReceiver();
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
                mTimeSource = new RoughTimeSource();
                invalidate();
            } else {
                mTimeSource = new ExactTimeSource();
                mHandler.sendEmptyMessage(MSG_UPDATE_TIME_CONTINUOUSLY);
            }
        }

        @Override
        public void onDraw(Canvas canvas, Rect bound) {
            final long time = mTimeSource.getTime();
            final float secAngle = (time % SEC_BASE) / (float) SEC_BASE * 360f - 90f;
            final float minAngle = (time % MIN_BASE) / (float) MIN_BASE * 360f - 90f;
            final float hourAngle = (time % HOUR_BASE) / (float) HOUR_BASE * 360f - 90f;
            onDrawClockFace(canvas, bound,
                    hourAngle, minAngle, secAngle, isInAmbientMode());
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                mHandler.sendEmptyMessage(MSG_UPDATE_TIME_CONTINUOUSLY);
                registerReceiver();
            } else {
                unregisterReceiver();
            }
        }
    }
}
