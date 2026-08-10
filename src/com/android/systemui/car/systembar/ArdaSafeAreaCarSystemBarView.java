/*
 * ARDA Projection Safe Area
 *
 * Keeps the CarSystemBar window/provider full-size while fitting the contents of
 * vertical left/right ARDA rail layouts inside WindowInsets.Type.systemOverlays().
 */
package com.android.systemui.car.systembar;

import android.content.Context;
import android.graphics.Insets;
import android.util.AttributeSet;
import android.util.Log;
import android.view.WindowInsets;

/**
 * ARDA-only CarSystemBarView variant used by the left/right rail layouts.
 *
 * The background/window remains full height. Only this view's content box is
 * reduced through padding, so existing status/navigation bar providers are not
 * resized or retyped.
 */
public class ArdaSafeAreaCarSystemBarView extends CarSystemBarView {
    private static final String TAG = "ArdaCarSystemBarInsets";

    private boolean mBasePaddingCaptured;
    private int mBasePaddingLeft;
    private int mBasePaddingTop;
    private int mBasePaddingRight;
    private int mBasePaddingBottom;

    public ArdaSafeAreaCarSystemBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        captureBasePaddingIfNeeded();
        requestApplyInsets();
    }

    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        final WindowInsets result = super.onApplyWindowInsets(insets);

        captureBasePaddingIfNeeded();

        if (getOrientation() != VERTICAL) {
            restoreBasePadding();
            Log.i(TAG, "SKIP orientation=HORIZONTAL"
                    + " instance=" + Integer.toHexString(System.identityHashCode(this)));
            return result;
        }

        final Insets safe = insets.getInsets(WindowInsets.Type.systemOverlays());

        setPadding(
                mBasePaddingLeft,
                mBasePaddingTop + safe.top,
                mBasePaddingRight,
                mBasePaddingBottom + safe.bottom);

        Log.i(TAG, "APPLY orientation=VERTICAL"
                + " top=" + safe.top
                + " bottom=" + safe.bottom
                + " baseTop=" + mBasePaddingTop
                + " baseBottom=" + mBasePaddingBottom
                + " paddingTop=" + getPaddingTop()
                + " paddingBottom=" + getPaddingBottom()
                + " width=" + getWidth()
                + " height=" + getHeight()
                + " instance=" + Integer.toHexString(System.identityHashCode(this)));

        return result;
    }

    private void captureBasePaddingIfNeeded() {
        if (mBasePaddingCaptured) {
            return;
        }
        mBasePaddingLeft = getPaddingLeft();
        mBasePaddingTop = getPaddingTop();
        mBasePaddingRight = getPaddingRight();
        mBasePaddingBottom = getPaddingBottom();
        mBasePaddingCaptured = true;
    }

    private void restoreBasePadding() {
        if (!mBasePaddingCaptured) {
            return;
        }
        setPadding(
                mBasePaddingLeft,
                mBasePaddingTop,
                mBasePaddingRight,
                mBasePaddingBottom);
    }
}
