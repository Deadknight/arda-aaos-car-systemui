/*
 * Copyright (C) 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

package com.android.systemui.car.systembar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.util.Log;

/** Right-rail widget button whose selected state follows the Launcher widget drawer. */
public final class ArdaWidgetSystemBarButton extends CarSystemBarButton {
    private static final String TAG = "ArdaWidgetButton";
    private static final String ACTION_WIDGET_DRAWER_STATE =
            "com.android.systemui.action.ARDA_WIDGET_DRAWER_STATE";
    private static final String EXTRA_WIDGET_DRAWER_OPEN = "open";
    private static final String CONTROL_LAYOUT_PERMISSION =
            "com.android.car.carlauncher.permission.CONTROL_LAYOUT";

    private boolean mReceiverRegistered;

    private final BroadcastReceiver mStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!ACTION_WIDGET_DRAWER_STATE.equals(intent.getAction())) {
                return;
            }
            boolean open = intent.getBooleanExtra(EXTRA_WIDGET_DRAWER_OPEN, false);
            setSelected(open);
            Log.i(TAG, "ARDA_WIDGET_BUTTON_SELECTED=" + open);
        }
    };

    public ArdaWidgetSystemBarButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!mReceiverRegistered) {
            getContext().registerReceiver(
                    mStateReceiver,
                    new IntentFilter(ACTION_WIDGET_DRAWER_STATE),
                    CONTROL_LAYOUT_PERMISSION,
                    null,
                    Context.RECEIVER_EXPORTED);
            mReceiverRegistered = true;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mReceiverRegistered) {
            getContext().unregisterReceiver(mStateReceiver);
            mReceiverRegistered = false;
        }
        super.onDetachedFromWindow();
    }
}
