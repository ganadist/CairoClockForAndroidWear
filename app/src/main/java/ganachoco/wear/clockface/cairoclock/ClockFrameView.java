package ganachoco.wear.clockface.cairoclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import java.util.TimeZone;

public class ClockFrameView extends FrameLayout {
    private Context mContext;
    private boolean mAttached;
    private long mTimezoneOffset;
    private ClockHandView[] mHands = new ClockHandView[3];
    private Animation mDimEnterAnim;
    private Animation mDimExitAnim;
    private static final String TAG = "CairoClockFrame";

    public ClockFrameView(Context context) {
        this(context, null);
    }

    public ClockFrameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mDimEnterAnim = AnimationUtils.loadAnimation(getContext(), R.anim.dim_hand_enter);
        mDimEnterAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }
            @Override
            public void onAnimationEnd(Animation animation) {
                setDimOnInner();
            }
            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
        mDimExitAnim = AnimationUtils.loadAnimation(getContext(), R.anim.dim_hand_exit);
        mDimExitAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                setDimOffInner();
            }
            @Override
            public void onAnimationRepeat(Animation animation) { }
            @Override
            public void onAnimationEnd(Animation animation) { }
        });
    }


    public ClockFrameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        mTimezoneOffset = TimeZone.getDefault().getRawOffset();
        mTimeSource = new RealTime();
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!mAttached) {
            Log.d(TAG, "onAttachedWindow");
            mAttached = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            mContext.registerReceiver(mTimezoneChangedReceiver, filter);
            mHandler.sendEmptyMessage(MSG_UPDATE_DRAW);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAttached) {
            Log.d(TAG, "onDetachedWindow");
            mAttached = false;
            mContext.unregisterReceiver(mTimezoneChangedReceiver);
            if (mDim) {
                mContext.unregisterReceiver(mTimeTickReceiver);
            }
            mHandler.removeMessages(MSG_UPDATE_DRAW);
        }
    }

    private void queueDraw() {
        for (int i=0; i < mHands.length; i++) {
            mHands[i].invalidate();
        }
        invalidate();
    }

    private final BroadcastReceiver mTimezoneChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                String tz = intent.getStringExtra("time-zone");
                mTimezoneOffset = TimeZone.getTimeZone(tz).getRawOffset();
                queueDraw();
            }
        }
    };

    private final BroadcastReceiver mTimeTickReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                queueDraw();
            }
        }
    };

    private static final int MSG_UPDATE_DRAW = 0;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MSG_UPDATE_DRAW:
                    sendEmptyMessageDelayed(MSG_UPDATE_DRAW, 100);
                    queueDraw();
            }
        }
    };

    private void setDimOnInner() {
        if (mDim == true)
            return;
        mHands[2].setVisibility(View.GONE);
        queueDraw();
        mHandler.removeMessages(MSG_UPDATE_DRAW);
        mContext.registerReceiver(mTimeTickReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        mDim = true;
    }

    private void setDimOffInner() {
        if (mDim == false)
            return;
        mHands[2].setVisibility(View.VISIBLE);
        mContext.unregisterReceiver(mTimeTickReceiver);
        mHandler.sendEmptyMessage(MSG_UPDATE_DRAW);
        mDim = false;
    }

    private boolean mDim;
    void setDim(boolean dim) {
        if (dim) {
            mHands[2].startAnimation(mDimEnterAnim);
        } else {
            mHands[2].startAnimation(mDimExitAnim);
        }
    }

    void setScreenOff(boolean off) {

    }

    static final int ID_HOUR_HAND = 6;
    static final int ID_HOUR_HAND_SHADOW = 7;
    static final int ID_MINUTE_HAND = 8;
    static final int ID_MINUTE_HAND_SHADOW = 9;
    static final int ID_SECOND_HAND = 10;
    static final int ID_SECOND_HAND_SHADOW = 11;

    private static final long SEC_BASE = 1000 * 60;
    private static final long MIN_BASE = 1000 * 60 * 60;
    private static final long HOUR_BASE = 1000 * 60 * 60 * 12;

    interface TimeSource {
        long getTime();
    }

    class RealTime implements TimeSource {
        public long getTime() {
            return System.currentTimeMillis() + mTimezoneOffset;
        }
    }

    class FakeTime implements TimeSource {
        private long mTime;
        public FakeTime(long time) {
            mTime = time;
        }
        public long getTime() {
            return mTime;
        }
    }

    private TimeSource mTimeSource;

    private ClockHandView setHandResource(int layoutId, final long angleBase, int shadow, int hand) {
        ClockHandView v = (ClockHandView)findViewById(layoutId);
        v.setAngle(new ClockHandView.ClockAngle() {
            @Override
            public float getAngle() {
                long time = mTimeSource.getTime();
                return ((time % angleBase) / (float) angleBase) * 360f - 90f;
            }
        });
        v.setResources(shadow, hand);
        return v;
    }

    void setFakeTime(long time) {
        mTimeSource = new FakeTime(time);
    }

    void setResources (int rids[]) {
        mHands[0] = setHandResource(R.id.hand_hour, HOUR_BASE, rids[ID_HOUR_HAND_SHADOW], rids[ID_HOUR_HAND]);
        mHands[1] = setHandResource(R.id.hand_minute, MIN_BASE, rids[ID_MINUTE_HAND_SHADOW], rids[ID_MINUTE_HAND]);
        mHands[2] = setHandResource(R.id.hand_second, SEC_BASE, rids[ID_SECOND_HAND_SHADOW], rids[ID_SECOND_HAND]);
    }

    void onDestroy() {
        for (int i = 0; i < mHands.length; i++) {
            if (mHands[i] != null) {
                mHands[i].onDestroy();
                mHands[i] = null;
            }
        }
    }
}
