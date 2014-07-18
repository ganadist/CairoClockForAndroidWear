package ganachoco.wear.clockface.cairoclock;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import android.support.wearable.view.WatchViewStub;

public abstract class BaseLayoutActivity extends WatchFaceActivity {
    private ClockFrameView mClockView;
    private static final String TAG = "CairoClock";

    protected abstract int[] getFrameResources();
    private ImageView[] mViews = new ImageView[6];

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
                    mViews[i].setImageResource(rids[i]);
                }
                mClockView = (ClockFrameView) stub.findViewById(R.id.clock_frame);
                mClockView.setResources(rids);
                Intent intent = getIntent();
                int time = intent.getIntExtra("time", -1);
                if (time != -1) {
                    int hour = time / 10000;
                    int min = (time /100 ) % 100;
                    int sec = time % 100;
                    long ts = ((hour * 60 + min) * 60 + sec ) * 1000;
                    mClockView.setFakeTime(ts);
                }
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
        Log.d(TAG, "Activity.onDestroy()");
        mClockView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onDimChanged(boolean dim) {
        mClockView.setDim(dim);
    }

    @Override
    protected void onBacklightChanged(boolean off) {

    }
}