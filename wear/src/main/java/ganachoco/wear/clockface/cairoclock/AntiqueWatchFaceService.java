package ganachoco.wear.clockface.cairoclock;

import android.support.wearable.watchface.WatchFaceStyle;

public class AntiqueWatchFaceService extends BaseWatchFaceService {
    @Override
    protected void setupWatchStyle(Engine engine) {
        final int resIds[] = {
                R.drawable.antique_clock_hour_hand_shadow,
                R.drawable.antique_clock_minute_hand_shadow,
                R.drawable.antique_clock_second_hand_shadow,
                R.drawable.antique_clock_hour_hand,
                R.drawable.antique_clock_minute_hand,
                R.drawable.antique_clock_second_hand,
                R.drawable.antique_clock_drop_shadow,
                R.drawable.antique_clock_face,
                R.drawable.antique_clock_marks,
                R.drawable.antique_clock_face_shadow,
                R.drawable.antique_clock_glass,
                R.drawable.antique_clock_frame,
        };
        loadBitmaps(resIds);
        engine.setWatchFaceStyle(new WatchFaceStyle.Builder(this)
                .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                .setBackgroundVisibility(WatchFaceStyle
                        .BACKGROUND_VISIBILITY_INTERRUPTIVE)
                .setShowSystemUiTime(false)
                .build());
    }
}