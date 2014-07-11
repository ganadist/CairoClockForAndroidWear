package ganachoco.wear.clockface.cairoclock;

import android.os.Bundle;

public class IpulseActivity extends BaseLayoutActivity {


    @Override
    protected int[] getFrameResources() {
        return new int[] {
            R.drawable.ipulse_clock_drop_shadow,
            R.drawable.ipulse_clock_face,
            R.drawable.ipulse_clock_marks,
            R.drawable.ipulse_clock_face_shadow,
            R.drawable.ipulse_clock_glass,
            R.drawable.ipulse_clock_frame,
            R.drawable.ipulse_clock_hour_hand,
            R.drawable.ipulse_clock_hour_hand_shadow,
            R.drawable.ipulse_clock_minute_hand,
            R.drawable.ipulse_clock_minute_hand_shadow,
            R.drawable.ipulse_clock_second_hand,
            R.drawable.ipulse_clock_second_hand_shadow
        };
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
