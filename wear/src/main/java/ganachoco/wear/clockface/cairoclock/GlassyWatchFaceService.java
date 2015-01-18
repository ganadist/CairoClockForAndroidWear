package ganachoco.wear.clockface.cairoclock;

public class GlassyWatchFaceService extends BaseWatchFaceService {
    @Override
    protected void setupWatchStyle(Engine engine) {
        final int resIds[] = {
                R.drawable.glassy_clock_hour_hand_shadow,
                R.drawable.glassy_clock_minute_hand_shadow,
                R.drawable.glassy_clock_second_hand_shadow,
                R.drawable.glassy_clock_hour_hand,
                R.drawable.glassy_clock_minute_hand,
                R.drawable.glassy_clock_second_hand,
                R.drawable.glassy_clock_drop_shadow,
                R.drawable.glassy_clock_face,
                R.drawable.glassy_clock_marks,
                R.drawable.glassy_clock_face_shadow,
                R.drawable.glassy_clock_glass,
                R.drawable.glassy_clock_frame,
        };
        loadBitmaps(resIds);
    }
}
