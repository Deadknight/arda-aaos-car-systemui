package com.android.systemui.car.systembar;

import android.content.Context;
import android.util.AttributeSet;

/** Right rail in Vertical mode, top bar in Horizontal mode. */
public final class ArdaStatusSystemBarView extends ArdaThemedCarSystemBarView {
    private static final String KEY = "arda_status_bar_background_color";

    public ArdaStatusSystemBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected String getArdaColorSettingKey() {
        return KEY;
    }

    @Override
    protected String getArdaBarRole() {
        return "STATUS_RIGHT_TOP";
    }
}
