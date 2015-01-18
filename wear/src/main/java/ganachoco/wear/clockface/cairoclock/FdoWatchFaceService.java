package ganachoco.wear.clockface.cairoclock;

public class FdoWatchFaceService extends BaseWatchFaceService {
    @Override
    protected void setupWatchStyle(Engine engine) {
        final int resIds[] = {
                R.drawable.fdo_clock_hour_hand_shadow,
                R.drawable.fdo_clock_minute_hand_shadow,
                R.drawable.fdo_clock_second_hand_shadow,
                R.drawable.fdo_clock_hour_hand,
                R.drawable.fdo_clock_minute_hand,
                R.drawable.fdo_clock_second_hand,
                R.drawable.fdo_clock_drop_shadow,
                R.drawable.fdo_clock_face,
                R.drawable.fdo_clock_marks,
                R.drawable.fdo_clock_face_shadow,
                R.drawable.fdo_clock_glass,
                R.drawable.fdo_clock_frame,
        };
        loadBitmaps(resIds);
    }
}
