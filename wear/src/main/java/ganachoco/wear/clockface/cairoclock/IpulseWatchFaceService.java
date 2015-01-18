package ganachoco.wear.clockface.cairoclock;

public class IpulseWatchFaceService extends BaseWatchFaceService {
    @Override
    protected void setupWatchStyle(Engine engine) {
        final int resIds[] = {
                R.drawable.ipulse_clock_hour_hand_shadow,
                R.drawable.ipulse_clock_minute_hand_shadow,
                R.drawable.ipulse_clock_second_hand_shadow,
                R.drawable.ipulse_clock_hour_hand,
                R.drawable.ipulse_clock_minute_hand,
                R.drawable.ipulse_clock_second_hand,
                R.drawable.ipulse_clock_drop_shadow,
                R.drawable.ipulse_clock_face,
                R.drawable.ipulse_clock_marks,
                R.drawable.ipulse_clock_face_shadow,
                R.drawable.ipulse_clock_glass,
                R.drawable.ipulse_clock_frame,
        };
        loadBitmaps(resIds);
    }
}
