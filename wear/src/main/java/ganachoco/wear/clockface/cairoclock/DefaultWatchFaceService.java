package ganachoco.wear.clockface.cairoclock;

public class DefaultWatchFaceService extends BaseWatchFaceService {
    @Override
    protected void setupWatchStyle(Engine engine) {
        final int resIds[] = {
                R.drawable.default_clock_hour_hand_shadow,
                R.drawable.default_clock_minute_hand_shadow,
                R.drawable.default_clock_second_hand_shadow,
                R.drawable.default_clock_hour_hand,
                R.drawable.default_clock_minute_hand,
                R.drawable.default_clock_second_hand,
                R.drawable.default_clock_drop_shadow,
                R.drawable.default_clock_face,
                R.drawable.default_clock_marks,
                R.drawable.default_clock_face_shadow,
                R.drawable.default_clock_glass,
                R.drawable.default_clock_frame,
        };
        loadBitmaps(resIds);
    }
}
