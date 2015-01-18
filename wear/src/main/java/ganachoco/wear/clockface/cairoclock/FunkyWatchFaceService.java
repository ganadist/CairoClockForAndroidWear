package ganachoco.wear.clockface.cairoclock;

public class FunkyWatchFaceService extends BaseWatchFaceService {
    @Override
    protected void setupWatchStyle(Engine engine) {
        final int resIds[] = {
                R.drawable.funky_clock_hour_hand_shadow,
                R.drawable.funky_clock_minute_hand_shadow,
                R.drawable.funky_clock_second_hand_shadow,
                R.drawable.funky_clock_hour_hand,
                R.drawable.funky_clock_minute_hand,
                R.drawable.funky_clock_second_hand,
                R.drawable.funky_clock_drop_shadow,
                R.drawable.funky_clock_face,
                R.drawable.funky_clock_marks,
                R.drawable.funky_clock_face_shadow,
                R.drawable.funky_clock_glass,
                R.drawable.funky_clock_frame,
        };
        loadBitmaps(resIds);
    }
}
