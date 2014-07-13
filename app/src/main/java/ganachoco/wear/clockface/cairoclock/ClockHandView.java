package ganachoco.wear.clockface.cairoclock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

public class ClockHandView extends View {
    private Matrix mMatrix = new Matrix();
    private Bitmap mBitmaps[] = new Bitmap[2];

    private static final String TAG = "CairoClock";
    private static final int ID_SHADOW = 0;
    private static final int ID_HAND = 1;

    private int mScreenWidth;

    public ClockHandView(Context context) {
        this(context, null, 0);
    }

    public ClockHandView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClockHandView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    void setResources(int shadow, int hand) {
        mBitmaps[ID_SHADOW] = BitmapFactory.decodeResource(getContext().getResources(), shadow);
        mBitmaps[ID_HAND] = BitmapFactory.decodeResource(getContext().getResources(), hand);
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
    }

    interface ClockAngle {
        float getAngle();
    }

    private ClockAngle mAngle;

    void setAngle(ClockAngle angle) {
        mAngle = angle;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getVisibility() == View.VISIBLE)
            drawClockHand(canvas, mAngle.getAngle());
    }

    private static final float SHADOW_OFFSET = 3f;

    private void drawClockHand(Canvas canvas, float angle) {
        float scale = 1.0f;
        int bitmapWidth = mBitmaps[0].getWidth();
        if (bitmapWidth != mScreenWidth) {
            scale = (float) mScreenWidth / (float) bitmapWidth;
        }

        {
            canvas.save();
            canvas.scale(scale, scale);
            {
                canvas.save();
                canvas.translate(SHADOW_OFFSET, SHADOW_OFFSET);
                {
                    canvas.save();
                    canvas.rotate(angle, bitmapWidth / 2.0f, bitmapWidth / 2.0f);
                    canvas.drawBitmap(mBitmaps[ID_SHADOW], mMatrix, null);
                    canvas.restore();
                }
                canvas.restore();
            }
            {
                canvas.save();
                canvas.rotate(angle, bitmapWidth / 2.0f, bitmapWidth / 2.0f);
                canvas.drawBitmap(mBitmaps[ID_HAND], mMatrix, null);
                canvas.restore();
            }
            canvas.restore();
        }
    }
    void onDestroy() {
        for (int i = 0; i < mBitmaps.length; i++) {
            if (mBitmaps[i] != null) {
                mBitmaps[i].recycle();
                mBitmaps[i] = null;
            }
        }
    }
}