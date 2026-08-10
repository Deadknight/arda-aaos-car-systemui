/*
 * Copyright (C) 2026 The Android Open Source Project
 * Licensed under the Apache License, Version 2.0.
 */
package com.android.systemui.car.systembar;

import android.annotation.LayoutRes;
import android.content.Context;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;

import com.android.systemui.R;

/** Selects ARDA system-bar layouts from runtime Global settings. */
public final class ArdaSystemBarViewSupplier implements CarSystemBarViewSupplier {
    public enum Side { TOP, LEFT, RIGHT }

    private static final String ARDA_BOTTOM_HVAC_VISIBLE =
            "arda_bottom_hvac_visible";
    private static final String ARDA_LEFT_RAIL_WHEN_HVAC_HIDDEN =
            "arda_left_rail_when_hvac_hidden";
    private static final String ARDA_RIGHT_STATUS_RAIL_ENABLED =
            "arda_right_status_rail_enabled";

    private final Side mSide;
    @LayoutRes private final int mSetupLayout;
    @LayoutRes private final int mNotSetupLayout;

    public ArdaSystemBarViewSupplier(
            Side side,
            @LayoutRes int setupLayout,
            @LayoutRes int notSetupLayout) {
        mSide = side;
        mSetupLayout = setupLayout;
        mNotSetupLayout = notSetupLayout;
    }

    @Override
    public ViewGroup getSystemBarView(Context context, boolean isSetUp) {
        int layoutId = chooseLayout(context, isSetUp);
        return (ViewGroup) View.inflate(context, layoutId, null);
    }

    @LayoutRes
    private int chooseLayout(Context context, boolean isSetUp) {
        boolean leftRailActive = isLeftRailActive(context);
        boolean rightStatusRailActive = isRightStatusRailActive(context);

        if (mSide == Side.TOP && leftRailActive && !rightStatusRailActive) {
            return R.layout.car_top_system_bar_arda_status_only;
        }
        if (mSide == Side.LEFT) {
            if (leftRailActive) {
                return R.layout.car_left_system_bar_arda_dock;
            }
            if (rightStatusRailActive) {
                return R.layout.car_left_system_bar_arda_back_only;
            }
        }
        if (mSide == Side.RIGHT && rightStatusRailActive) {
            return R.layout.car_right_system_bar_arda_status;
        }
        return isSetUp ? mSetupLayout : mNotSetupLayout;
    }

    private static boolean isLeftRailActive(Context context) {
        boolean featureEnabled = Settings.Global.getInt(
                context.getContentResolver(),
                ARDA_LEFT_RAIL_WHEN_HVAC_HIDDEN,
                0) != 0;
        boolean bottomHvacVisible = Settings.Global.getInt(
                context.getContentResolver(),
                ARDA_BOTTOM_HVAC_VISIBLE,
                1) != 0;
        return featureEnabled && !bottomHvacVisible;
    }

    private static boolean isRightStatusRailActive(Context context) {
        return Settings.Global.getInt(
                context.getContentResolver(),
                ARDA_RIGHT_STATUS_RAIL_ENABLED,
                0) != 0;
    }
}
