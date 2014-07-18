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
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import java.util.TimeZone;

public class ClockView extends View {
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

    private static final String TAG = "CairoClock";

    private Context mContext;
    private boolean mAttached;
    private long mTimezoneOffset;

    private int mScreenWidth;
    private int mScreenHeight;
    private boolean mDim;

    private static final int MSG_UPDATE_DRAW = 0;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MSG_UPDATE_DRAW:
                    sendEmptyMessageDelayed(MSG_UPDATE_DRAW, 100);
                    invalidate();
            }
        }
    };

    public ClockView(Context context) {
        this(context, null, 0);
    }

    public ClockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        mTimezoneOffset = TimeZone.getDefault().getRawOffset();
    }

    void setResources(int[] ids) {
        for (int i = ID_HOUR_HAND; i < ID_MAX; i++) {
            mBitmaps[i] = BitmapFactory.decodeResource(mContext.getResources(), ids[i]);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!mAttached) {
            mAttached = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            mContext.registerReceiver(mTimezoneChangedReceiver, filter, null, mHandler);
            DisplayMetrics metrics = new DisplayMetrics();
            ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
            mScreenWidth = metrics.widthPixels;
            mScreenHeight = metrics.heightPixels;
            mHandler.sendEmptyMessage(MSG_UPDATE_DRAW);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAttached) {
            mAttached = false;
            getContext().unregisterReceiver(mTimezoneChangedReceiver);
            mHandler.removeMessages(MSG_UPDATE_DRAW);
            if (mDim) {
                mContext.unregisterReceiver(mTimeTickReceiver);
                mDim = false;
            }
        }
    }

    private final BroadcastReceiver mTimezoneChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                String tz = intent.getStringExtra("time-zone");
                mTimezoneOffset = TimeZone.getTimeZone(tz).getRawOffset();
            }
        }
    };

    private final BroadcastReceiver mTimeTickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                invalidate();
            }
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        long time = System.currentTimeMillis() + mTimezoneOffset;
        drawClock(canvas, time);
    }

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
                if (!mDim){
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
            if (!mDim) {
                canvas.save();
                canvas.rotate(secAngle, bitmapWidth / 2.0f, bitmapWidth / 2.0f);
                canvas.drawBitmap(mBitmaps[ID_SECOND_HAND], mMatrix, null);
                canvas.restore();
            }
            canvas.restore();
        }
    }

    void setDim(boolean dim) {
        if (mDim == dim) return;
        if (dim) {
            mDim = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            mContext.registerReceiver(mTimeTickReceiver, filter);
            mHandler.removeMessages(MSG_UPDATE_DRAW);
        } else {
            mDim = false;
            mContext.unregisterReceiver(mTimeTickReceiver);
            mHandler.sendEmptyMessage(MSG_UPDATE_DRAW);
        }
        invalidate();
    }
    @Override
    protected void finalize() {
        for (int i = ID_HOUR_HAND; i < ID_MARKS; i++) {
            mBitmaps[i].recycle();
        }
        try {
            super.finalize();
        } catch (Throwable throwable) {
        }
    }
}