package ganachoco.wear.clockface.cairoclock;

public class TangoActivity extends BaseLayoutActivity {
    @Override
    protected int[] getFrameResources() {
        return new int[] {
            R.drawable.tango_clock_drop_shadow,
            R.drawable.tango_clock_face,
            R.drawable.tango_clock_marks,
            R.drawable.tango_clock_face_shadow,
            R.drawable.tango_clock_glass,
            R.drawable.tango_clock_frame,
            R.drawable.tango_clock_hour_hand,
            R.drawable.tango_clock_hour_hand_shadow,
            R.drawable.tango_clock_minute_hand,
            R.drawable.tango_clock_minute_hand_shadow,
            R.drawable.tango_clock_second_hand,
            R.drawable.tango_clock_second_hand_shadow
        };
    }
}
