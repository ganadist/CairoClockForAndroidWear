package ganachoco.wear.clockface.cairoclock;

public class GlassyActivity extends BaseLayoutActivity {
    @Override
    protected int[] getFrameResources() {
        return new int[] {
            R.drawable.glassy_clock_drop_shadow,
            R.drawable.glassy_clock_face,
            R.drawable.glassy_clock_marks,
            R.drawable.glassy_clock_face_shadow,
            R.drawable.glassy_clock_glass,
            R.drawable.glassy_clock_frame,
            R.drawable.glassy_clock_hour_hand,
            R.drawable.glassy_clock_hour_hand_shadow,
            R.drawable.glassy_clock_minute_hand,
            R.drawable.glassy_clock_minute_hand_shadow,
            R.drawable.glassy_clock_second_hand,
            R.drawable.glassy_clock_second_hand_shadow
        };
    }

    @Override
    protected float getFrameScale() {
        return 1.f;
    }
}
