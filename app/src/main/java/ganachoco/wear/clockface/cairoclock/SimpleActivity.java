package ganachoco.wear.clockface.cairoclock;

import android.os.Bundle;

public class SimpleActivity extends BaseLayoutActivity {


    @Override
    protected int[] getFrameResources() {
        return new int[] {
            R.drawable.simple_clock_drop_shadow,
            R.drawable.simple_clock_face,
            R.drawable.simple_clock_marks,
            R.drawable.simple_clock_face_shadow,
            R.drawable.simple_clock_glass,
            R.drawable.simple_clock_frame,
            R.drawable.simple_clock_hour_hand,
            R.drawable.simple_clock_hour_hand_shadow,
            R.drawable.simple_clock_minute_hand,
            R.drawable.simple_clock_minute_hand_shadow,
            R.drawable.simple_clock_second_hand,
            R.drawable.simple_clock_second_hand_shadow
        };
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
