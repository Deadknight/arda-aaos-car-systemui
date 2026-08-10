/*
 * Copyright (C) 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.systemui.car.hvac;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;

import com.android.systemui.car.systembar.element.CarSystemBarElementController;
import com.android.systemui.car.systembar.element.CarSystemBarElementStateController;
import com.android.systemui.car.systembar.element.CarSystemBarElementStatusBarDisableController;
import com.android.systemui.dagger.qualifiers.Main;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

/**
 * A CarSystemBarElementController for handling hvac button interactions.
 */
public class TemperatureControlViewController extends
            CarSystemBarElementController<TemperatureControlView> {
    private static final String ARDA_BOTTOM_HVAC_VISIBLE = "arda_bottom_hvac_visible";

    private final HvacPanelOverlayViewController mHvacPanelOverlayViewController;
    private final HvacController mHvacController;
    private final ContentResolver mContentResolver;
    private final ContentObserver mVisibilityObserver;

    private boolean mBottomHvacVisible;
    private boolean mVisibilityObserverRegistered;

    @AssistedInject
    public TemperatureControlViewController(@Assisted TemperatureControlView hvacButton,
            CarSystemBarElementStatusBarDisableController disableController,
            CarSystemBarElementStateController stateController,
            Context context,
            @Main Handler mainHandler,
            HvacPanelOverlayViewController hvacPanelOverlayViewController,
            HvacController hvacController) {
        super(hvacButton, disableController, stateController);

        mHvacPanelOverlayViewController = hvacPanelOverlayViewController;
        mHvacController = hvacController;
        mContentResolver = context.getContentResolver();
        mBottomHvacVisible = readBottomHvacVisible();
        mVisibilityObserver = new ContentObserver(mainHandler) {
            @Override
            public void onChange(boolean selfChange) {
                refreshVisibility();
            }
        };
        hvacButton.setTemperatureTextClickListener(this::onHvacClick);
    }

    @AssistedFactory
    public interface Factory extends
            CarSystemBarElementController.Factory<TemperatureControlView,
                    TemperatureControlViewController> {
    }

    @Override
    protected void onViewAttached() {
        super.onViewAttached();
        mHvacController.registerHvacViews(mView);
        if (!mVisibilityObserverRegistered) {
            mContentResolver.registerContentObserver(
                    Settings.Global.getUriFor(ARDA_BOTTOM_HVAC_VISIBLE),
                    false,
                    mVisibilityObserver);
            mVisibilityObserverRegistered = true;
        }
        refreshVisibility();
    }

    @Override
    protected void onViewDetached() {
        if (mVisibilityObserverRegistered) {
            mContentResolver.unregisterContentObserver(mVisibilityObserver);
            mVisibilityObserverRegistered = false;
        }
        mHvacController.unregisterViews(mView);
        super.onViewDetached();
    }

    @Override
    protected boolean shouldBeVisible() {
        return mBottomHvacVisible;
    }

    private boolean readBottomHvacVisible() {
        return Settings.Global.getInt(
                mContentResolver,
                ARDA_BOTTOM_HVAC_VISIBLE,
                /* def= */ 1) != 0;
    }

    private void refreshVisibility() {
        mBottomHvacVisible = readBottomHvacVisible();
        if (!mBottomHvacVisible) {
            mHvacPanelOverlayViewController.closeIfOpen();
        }
        updateVisibility();
    }

    private void onHvacClick(View v) {
        if (!mBottomHvacVisible) {
            return;
        }
        mHvacPanelOverlayViewController.toggle();
    }
}
