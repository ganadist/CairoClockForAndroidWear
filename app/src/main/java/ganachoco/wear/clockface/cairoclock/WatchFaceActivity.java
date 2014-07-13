package ganachoco.wear.clockface.cairoclock;

import android.app.Activity;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.view.Display;

public class WatchFaceActivity extends Activity implements DisplayManager.DisplayListener {
    private boolean mDim;
    private boolean mBacklightOff;
    private DisplayManager mDisplayManager;
    @Override
    public void onDisplayAdded(int displayId) {}

    @Override
    public void onDisplayRemoved(int displayId) {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDisplayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        mDisplayManager.registerDisplayListener(this, null);
    }
    @Override
    protected void onDestroy() {
        mDisplayManager.unregisterDisplayListener(this);
        super.onDestroy();
    }

    protected void onDimChanged(boolean dim) {

    }
    protected void onBacklightChanged(boolean off) {

    }

    @Override
    public void onDisplayChanged(int displayId) {
        if (displayId != Display.DEFAULT_DISPLAY)
            return;

        switch(mDisplayManager.getDisplay(displayId).getState()) {
            case Display.STATE_DOZING:
                // go to dim
                if (!mDim) {
                    mDim = true;
                    onDimChanged(mDim);
                }
                break;
            case Display.STATE_OFF:
                // go to screen off
                if (!mBacklightOff) {
                    mBacklightOff = true;
                    onBacklightChanged(mBacklightOff);
                }
                break;
            default:
                if (mDim) {
                    mDim = false;
                    onDimChanged(mDim);
                }
                if (mBacklightOff) {
                    mBacklightOff = false;
                    onBacklightChanged(mBacklightOff);
                }
                break;
        }
    }
}
