/** currently it is not used */

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
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.TimeZone;

public class ClockSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private Matrix mMatrix = new Matrix();
    private Paint mPaint = new Paint();

    static final int ID_DROP_SHADOW = 0;
    static final int ID_FACE = 1;
    static final int ID_MARKS = 2;
    static final int ID_FACE_SHADOW = 3;
    static final int ID_GLASS = 4;
    static final int ID_FRAME = 5;
    static final int ID_HOUR_HAND = 6;
    static final int ID_HOUR_HAND_SHADOW = 7;
    static final int ID_MINUTE_HAND = 8;
    static final int ID_MINUTE_HAND_SHADOW = 9;
    static final int ID_SECOND_HAND = 10;
    static final int ID_SECOND_HAND_SHADOW = 11;
    static final int ID_MAX = 12;

    private Bitmap mBitmaps[] = new Bitmap[ID_MAX];
    private Bitmap mBackgroundBitmap;
    private Bitmap mForegroundBitmap;

    private static final String TAG = "CairoClock";

    private Context mContext;
    private boolean mAttached;
    private long mTimezoneOffset;

    private int mScreenWidth;
    private int mScreenHeight;

    private final Handler mHandler = new Handler();

    public ClockSurfaceView(Context context) {
        this(context, null, 0);
    }

    public ClockSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockSurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        mTimezoneOffset = TimeZone.getDefault().getRawOffset();
        mPaint.setARGB(255, 255, 255, 255);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
    }

    void setResources(int[] ids) {
        for (int i = 0; i < ID_MAX; i++) {
            mBitmaps[i] = BitmapFactory.decodeResource(mContext.getResources(), ids[i]);
        }
        mBackgroundBitmap = Bitmap.createBitmap(mBitmaps[ID_DROP_SHADOW].getWidth(),
                mBitmaps[ID_DROP_SHADOW].getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBackgroundBitmap);
        canvas.drawBitmap(mBitmaps[ID_DROP_SHADOW], mMatrix, null);
        canvas.drawBitmap(mBitmaps[ID_FACE], mMatrix, null);
        canvas.drawBitmap(mBitmaps[ID_MARKS], mMatrix, null);

        mForegroundBitmap = Bitmap.createBitmap(mBitmaps[ID_DROP_SHADOW].getWidth(),
                mBitmaps[ID_DROP_SHADOW].getHeight(),
                Bitmap.Config.ARGB_8888);
        canvas = new Canvas(mForegroundBitmap);
        canvas.drawBitmap(mBitmaps[ID_FACE_SHADOW], mMatrix, null);
        canvas.drawBitmap(mBitmaps[ID_GLASS], mMatrix, null);
        canvas.drawBitmap(mBitmaps[ID_FRAME], mMatrix, null);
        for (int i = 0; i <= ID_FRAME ; i++) {
            mBitmaps[i].recycle();
            mBitmaps[i] = null;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!mAttached) {
            mAttached = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            mContext.registerReceiver(mIntentReceiver, filter, null, mHandler);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAttached) {
            mAttached = false;
            getContext().unregisterReceiver(mIntentReceiver);
        }
    }

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                String tz = intent.getStringExtra("time-zone");
                mTimezoneOffset = TimeZone.getTimeZone(tz).getRawOffset();
            }
        }
    };

    private static final long SEC_BASE = 1000 * 60;
    private static final long MIN_BASE = 1000 * 60 * 60;
    private static final long HOUR_BASE = 1000 * 60 * 60 * 12;

    private void drawClock(Canvas canvas, long time) {
        float secAngle = ((time % SEC_BASE) / (float) SEC_BASE) * 360f - 90f;
        float minAngle = ((time % MIN_BASE) / (float) MIN_BASE) * 360f - 90f;
        float hourAngle = ((time % HOUR_BASE) / (float) HOUR_BASE) * 360f - 90f;
        final float shadowOffset = 3f;

        float scale = 1.0f;
        int bitmapWidth = mBitmaps[ID_HOUR_HAND].getWidth();
        if (bitmapWidth != mScreenWidth) {
            scale = (float) mScreenWidth / (float) bitmapWidth;
        }

        {
            canvas.save();
            canvas.scale(scale, scale);
            {
                canvas.drawBitmap(mBackgroundBitmap, mMatrix, mPaint);
                canvas.save();
                canvas.translate(shadowOffset, shadowOffset);
                {
                    canvas.save();
                    canvas.rotate(hourAngle, bitmapWidth / 2.0f, bitmapWidth / 2.0f);
                    canvas.drawBitmap(mBitmaps[ID_HOUR_HAND_SHADOW], mMatrix, null);
                    canvas.restore();
                }
                {
                    canvas.save();
                    canvas.rotate(minAngle, bitmapWidth / 2.0f, bitmapWidth / 2.0f);
                    canvas.drawBitmap(mBitmaps[ID_MINUTE_HAND_SHADOW], mMatrix, null);
                    canvas.restore();
                }
                {
                    canvas.save();
                    canvas.rotate(secAngle, bitmapWidth / 2.0f, bitmapWidth / 2.0f);
                    canvas.drawBitmap(mBitmaps[ID_SECOND_HAND_SHADOW], mMatrix, null);
                    canvas.restore();
                }
                canvas.restore();
            }
            {
                canvas.save();
                canvas.rotate(hourAngle, bitmapWidth / 2.0f, bitmapWidth / 2.0f);
                canvas.drawBitmap(mBitmaps[ID_HOUR_HAND], mMatrix, null);
                canvas.restore();
            }
            {
                canvas.save();
                canvas.rotate(minAngle, bitmapWidth / 2.0f, bitmapWidth / 2.0f);
                canvas.drawBitmap(mBitmaps[ID_MINUTE_HAND], mMatrix, null);
                canvas.restore();
            }
            {
                canvas.save();
                canvas.rotate(secAngle, bitmapWidth / 2.0f, bitmapWidth / 2.0f);
                canvas.drawBitmap(mBitmaps[ID_SECOND_HAND], mMatrix, null);
                canvas.restore();
            }
            canvas.drawBitmap(mForegroundBitmap, mMatrix, null);

            canvas.restore();
        }
    }

    @Override
    protected void finalize() {
        for (int i = ID_HOUR_HAND; i < ID_MARKS; i++) {
            mBitmaps[i].recycle();
        }
        mBackgroundBitmap.recycle();
        mForegroundBitmap.recycle();
        try {
            super.finalize();
        } catch (Throwable throwable) {
        }
    }

    private boolean mRun;
    private boolean mPause;
    private boolean mConfigured;
    private Thread mThread;

    class DrawThread extends Thread {
        public DrawThread() {
            super();
        }

        @Override
        public void run() {
            while (mRun) {
                SurfaceHolder h = getHolder();
                if (mConfigured && !mPause) {
                    long time = System.currentTimeMillis() + mTimezoneOffset;
                    Canvas canvas = h.lockCanvas();
                    if (canvas != null) {
                        drawClock(canvas, time);
                        h.unlockCanvasAndPost(canvas);
                    }
                }

                try {
                    Thread.sleep(100, 0);
                } catch (InterruptedException e) {
                }
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surface created");
        mRun = true;
        mConfigured = false;
        mThread = new DrawThread();
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surface changed");
        mScreenWidth = width;
        mScreenHeight = height;
        mConfigured = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surface destroyed");
        mRun = false;
        mConfigured = false;
        try {
            mThread.join();
        } catch (InterruptedException e) {
        }
    }
    void setPause(boolean pause) {
        mPause = pause;
    }
}