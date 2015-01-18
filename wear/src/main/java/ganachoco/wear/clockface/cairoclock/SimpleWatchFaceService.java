package ganachoco.wear.clockface.cairoclock;

public class SimpleWatchFaceService extends BaseWatchFaceService {
    @Override
    protected void setupWatchStyle(Engine engine) {
        final int resIds[] = {
                R.drawable.simple_clock_hour_hand_shadow,
                R.drawable.simple_clock_minute_hand_shadow,
                R.drawable.simple_clock_second_hand_shadow,
                R.drawable.simple_clock_hour_hand,
                R.drawable.simple_clock_minute_hand,
                R.drawable.simple_clock_second_hand,
                R.drawable.simple_clock_drop_shadow,
                R.drawable.simple_clock_face,
                R.drawable.simple_clock_marks,
                R.drawable.simple_clock_face_shadow,
                R.drawable.simple_clock_glass,
                R.drawable.simple_clock_frame,
        };
        loadBitmaps(resIds);
    }
}
