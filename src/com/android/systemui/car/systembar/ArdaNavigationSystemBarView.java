package com.android.systemui.car.systembar;

import android.content.Context;
import android.util.AttributeSet;

/** Left rail in Vertical mode, bottom bar in Horizontal mode. */
public final class ArdaNavigationSystemBarView extends ArdaThemedCarSystemBarView {
    private static final String KEY = "arda_navigation_bar_background_color";

    public ArdaNavigationSystemBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected String getArdaColorSettingKey() {
        return KEY;
    }

    @Override
    protected String getArdaBarRole() {
        return "NAVIGATION_LEFT_BOTTOM";
    }
}
