package ganachoco.wear.clockface.cairoclock;

public class ZenWatchFaceService extends BaseWatchFaceService {
    @Override
    protected void setupWatchStyle(Engine engine) {
        final int resIds[] = {
                R.drawable.zen_clock_hour_hand_shadow,
                R.drawable.zen_clock_minute_hand_shadow,
                R.drawable.zen_clock_second_hand_shadow,
                R.drawable.zen_clock_hour_hand,
                R.drawable.zen_clock_minute_hand,
                R.drawable.zen_clock_second_hand,
                R.drawable.zen_clock_drop_shadow,
                R.drawable.zen_clock_face,
                R.drawable.zen_clock_marks,
                R.drawable.zen_clock_face_shadow,
                R.drawable.zen_clock_glass,
                R.drawable.zen_clock_frame,
        };
        loadBitmaps(resIds);
    }
}
