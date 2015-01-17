package ganachoco.wear.clockface.cairoclock;

import android.support.wearable.watchface.WatchFaceStyle;

public class GremlinWatchFaceService extends BaseWatchFaceService {
    @Override
    protected void setupWatchStyle(Engine engine) {

        int resIds[] = {
                R.drawable.gremlin_clock_hour_hand_shadow,
                R.drawable.gremlin_clock_minute_hand_shadow,
                R.drawable.gremlin_clock_second_hand_shadow,
                R.drawable.gremlin_clock_hour_hand,
                R.drawable.gremlin_clock_minute_hand,
                R.drawable.gremlin_clock_second_hand,
                R.drawable.gremlin_clock_drop_shadow,
                R.drawable.gremlin_clock_face,
                R.drawable.gremlin_clock_marks,
                R.drawable.gremlin_clock_face_shadow,
                R.drawable.gremlin_clock_glass,
                R.drawable.gremlin_clock_frame,
        };
        loadBitmaps(resIds);
        engine.setWatchFaceStyle(new WatchFaceStyle.Builder(this)
                .setCardPeekMode(WatchFaceStyle.PEEK_OPACITY_MODE_TRANSLUCENT)
                .setBackgroundVisibility(WatchFaceStyle
                        .BACKGROUND_VISIBILITY_INTERRUPTIVE)
                .setShowSystemUiTime(false)
                .build());
    }
}
