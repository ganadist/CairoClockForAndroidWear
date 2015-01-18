package ganachoco.wear.clockface.cairoclock;

public class RadiumWatchFaceService extends BaseWatchFaceService {
    @Override
    protected void setupWatchStyle(Engine engine) {
        final int resIds[] = {
                R.drawable.radium_clock_hour_hand_shadow,
                R.drawable.radium_clock_minute_hand_shadow,
                R.drawable.radium_clock_second_hand_shadow,
                R.drawable.radium_clock_hour_hand,
                R.drawable.radium_clock_minute_hand,
                R.drawable.radium_clock_second_hand,
                R.drawable.radium_clock_drop_shadow,
                R.drawable.radium_clock_face,
                R.drawable.radium_clock_marks,
                R.drawable.radium_clock_face_shadow,
                R.drawable.radium_clock_glass,
                R.drawable.radium_clock_frame,
        };
        loadBitmaps(resIds);
    }
}
