package com.android.systemui.car.systembar;

import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Adds runtime background color control to ARDA CarSystemBar views.
 *
 * The safe-area behavior remains owned by ArdaSafeAreaCarSystemBarView.
 */
public abstract class ArdaThemedCarSystemBarView extends ArdaSafeAreaCarSystemBarView {
    private static final String TAG = "ArdaSystemBarTheme";
    private static final String KEY_REVISION = "arda_visual_theme_revision";

    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final ContentObserver mObserver = new ContentObserver(mHandler) {
        @Override
        public void onChange(boolean selfChange) {
            applyArdaBarTheme();
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            applyArdaBarTheme();
        }
    };

    private Drawable mOriginalBackground;
    private boolean mOriginalCaptured;
    private boolean mRegistered;

    public ArdaThemedCarSystemBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected abstract String getArdaColorSettingKey();
    protected abstract String getArdaBarRole();

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (!mOriginalCaptured) {
            mOriginalBackground = getBackground();
            mOriginalCaptured = true;
        }

        if (!mRegistered) {
            getContext().getContentResolver().registerContentObserver(
                    Settings.Global.getUriFor(KEY_REVISION),
                    false,
                    mObserver);
            mRegistered = true;
        }

        applyArdaBarTheme();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mRegistered) {
            getContext().getContentResolver().unregisterContentObserver(mObserver);
            mRegistered = false;
        }
        super.onDetachedFromWindow();
    }

    private void applyArdaBarTheme() {
        String value = Settings.Global.getString(
                getContext().getContentResolver(),
                getArdaColorSettingKey());

        if (value == null || value.isEmpty() || "default".equalsIgnoreCase(value)) {
            setBackground(mOriginalBackground);
            Log.i(TAG, "APPLY role=" + getArdaBarRole() + " value=default");
            return;
        }

        try {
            int color = Color.parseColor(value);
            setBackgroundColor(color);
            Log.i(TAG, "APPLY role=" + getArdaBarRole() + " value=" + value);
        } catch (RuntimeException e) {
            setBackground(mOriginalBackground);
            Log.e(TAG, "COLOR_PARSE_FAIL role=" + getArdaBarRole()
                    + " value=" + value, e);
        }
    }
}
