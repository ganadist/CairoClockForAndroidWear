package ganachoco.wear.clockface.cairoclock;

import android.app.Activity;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.ImageView;

public abstract class BaseLayoutActivity extends Activity implements DisplayManager.DisplayListener {
    private ClockView mClockView;
    private static final String TAG = "CairoClock";

    private DisplayManager mDisplayManager;

    protected abstract int[] getFrameResources();
    private View[] mViews = new ImageView[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Activity.onCreate()");
        super.onCreate(savedInstanceState);
        mDisplayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        mDisplayManager.registerDisplayListener(this, null);
        setContentView(R.layout.base_layout);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                Log.d(TAG, "onLayoutInflated called");
                int rids[] = getFrameResources();
                int[] wids = {
                        R.id.drop_shadow,
                        R.id.face,
                        R.id.marks,
                        R.id.face_shadow,
                        R.id.glass,
                        R.id.frame
                };
                for (int i = 0; i <= ClockView.ID_FRAME; i++) {
                    mViews[i] = (ImageView) stub.findViewById(wids[i]);
                    ((ImageView)mViews[i]).setImageResource(rids[i]);
                }
                mClockView = (ClockView) stub.findViewById(R.id.clock);
                mClockView.setResources(rids);
            }
        });
    }


    @Override
    protected void onPause() {
        Log.d(TAG, "Activity.onPause()");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "Activity.onResume()");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mDisplayManager.unregisterDisplayListener(this);
        super.onDestroy();
    }

    @Override
    public void onDisplayRemoved(int displayId) {

    }

    @Override
    public void onDisplayAdded(int displayId) {

    }

    private void enterDimAnimation(final View v) {
        Animation ani = AnimationUtils.loadAnimation(this, R.anim.dim_enter);
        ani.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) { }

                @Override
                public void onAnimationEnd(Animation animation) {
                    v.setAlpha(0.6f);
                    if (v == mClockView) mClockView.setDim(true);
                }

                @Override
                public void onAnimationRepeat(Animation animation) { }
            });
            v.startAnimation(ani);
    }

    private void exitDimAnimation(final View v) {
        Animation ani = AnimationUtils.loadAnimation(this, R.anim.dim_exit);
        ani.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (v == mClockView) mClockView.setDim(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.setAlpha(1f);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });
        v.startAnimation(ani);
    }

    private boolean mDim;
    @Override
    public void onDisplayChanged(int displayId) {

        switch(mDisplayManager.getDisplay(displayId).getState()){
            case Display.STATE_DOZING:
                // go to dim
                if (!mDim) {
                    mDim = true;
                    for (int i = 0; i < mViews.length; i++)
                        enterDimAnimation(mViews[i]);
                    enterDimAnimation(mClockView);
                }
                break;
            case Display.STATE_OFF:
                // go to screen off
                break;
            default:
                //  Not really sure what to so about Display.STATE_UNKNOWN, so
                //  we'll treat it as if the screen is normal.
                // screen on
                if (mDim) {
                    mDim = false;
                    for (int i = 0; i < mViews.length; i++)
                        exitDimAnimation(mViews[i]);
                    exitDimAnimation(mClockView);
                }
                break;
        }
    }

}