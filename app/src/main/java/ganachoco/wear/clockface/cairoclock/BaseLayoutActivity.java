package ganachoco.wear.clockface.cairoclock;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public abstract class BaseLayoutActivity extends WatchFaceActivity {
    private ClockFrameView mClockView;
    private static final String TAG = "CairoClock";

    protected abstract int[] getFrameResources();
    private View[] mViews = new ImageView[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Activity.onCreate()");
        super.onCreate(savedInstanceState);
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
                mClockView = (ClockFrameView) stub.findViewById(R.id.clock_frame);
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
    protected void onDimChanged(boolean dim) {
        mClockView.setDim(dim);
    }

    @Override
    protected void onBacklightChanged(boolean off) {

    }
}