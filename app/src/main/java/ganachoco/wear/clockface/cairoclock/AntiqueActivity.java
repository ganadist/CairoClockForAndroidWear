package ganachoco.wear.clockface.cairoclock;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

public class AntiqueActivity extends BaseLayoutActivity {


    @Override
    protected int[] getFrameResources() {
        return new int[] {
            R.drawable.antique_clock_drop_shadow,
            R.drawable.antique_clock_face,
            R.drawable.antique_clock_marks,
            R.drawable.antique_clock_face_shadow,
            R.drawable.antique_clock_glass,
            R.drawable.antique_clock_frame,
            R.drawable.antique_clock_hour_hand,
            R.drawable.antique_clock_hour_hand_shadow,
            R.drawable.antique_clock_minute_hand,
            R.drawable.antique_clock_minute_hand_shadow,
            R.drawable.antique_clock_second_hand,
            R.drawable.antique_clock_second_hand_shadow
        };
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
