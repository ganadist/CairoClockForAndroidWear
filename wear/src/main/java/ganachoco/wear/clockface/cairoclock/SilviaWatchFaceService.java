package ganachoco.wear.clockface.cairoclock;

public class SilviaWatchFaceService extends BaseWatchFaceService {
    @Override
    protected void setupWatchStyle(Engine engine) {
        final int resIds[] = {
                R.drawable.silvia_clock_hour_hand_shadow,
                R.drawable.silvia_clock_minute_hand_shadow,
                R.drawable.silvia_clock_second_hand_shadow,
                R.drawable.silvia_clock_hour_hand,
                R.drawable.silvia_clock_minute_hand,
                R.drawable.silvia_clock_second_hand,
                R.drawable.silvia_clock_drop_shadow,
                R.drawable.silvia_clock_face,
                R.drawable.silvia_clock_marks,
                R.drawable.silvia_clock_face_shadow,
                R.drawable.silvia_clock_glass,
                R.drawable.silvia_clock_frame,
        };
        loadBitmaps(resIds);
    }
}
