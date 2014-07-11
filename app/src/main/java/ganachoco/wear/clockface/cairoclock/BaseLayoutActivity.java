package ganachoco.wear.clockface.cairoclock;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.ImageView;

public class BaseLayoutActivity extends Activity {


    private ClockView mClockView;

    private static final String TAG = "CairoClock";

    protected int[] getFrameResources() {
        return null;
    }

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
                int ids[] = getFrameResources();
                ((ImageView) stub.findViewById(R.id.drop_shadow)).setImageResource(ids[ClockView.ID_DROP_SHADOW]);
                ((ImageView) stub.findViewById(R.id.face)).setImageResource(ids[ClockView.ID_FACE]);
                ((ImageView) stub.findViewById(R.id.marks)).setImageResource(ids[ClockView.ID_MARKS]);

                ((ImageView) stub.findViewById(R.id.face_shadow)).setImageResource(ids[ClockView.ID_FACE_SHADOW]);
                ((ImageView) stub.findViewById(R.id.glass)).setImageResource(ids[ClockView.ID_GLASS]);
                ((ImageView) stub.findViewById(R.id.frame)).setImageResource(ids[ClockView.ID_FRAME]);

                mClockView = (ClockView) stub.findViewById(R.id.clock);
                mClockView.setResources(ids);

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

        super.onDestroy();
    }
}
