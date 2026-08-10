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
package com.android.systemui.car.systembar;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;
import android.view.View;


import com.android.systemui.car.systembar.element.CarSystemBarElementController;
import com.android.systemui.car.systembar.element.CarSystemBarElementStateController;
import com.android.systemui.car.systembar.element.CarSystemBarElementStatusBarDisableController;
import com.android.systemui.car.users.CarSystemUIUserUtil;
import com.android.systemui.car.wm.scalableui.EventDispatcher;
import com.android.systemui.settings.UserTracker;

import java.util.List;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

/**
 * A CarSystemBarElementController for handling Home button interactions.
 */
public class HomeButtonController extends CarSystemBarButtonController  {
    private static final String ACTION_TOGGLE_MAP_FULLSCREEN =
            "com.android.car.carlauncher.action.TOGGLE_MAP_FULLSCREEN";
    private static final String CAR_LAUNCHER_PACKAGE = "com.android.car.carlauncher";
    private static final String CAR_LAUNCHER_HOME_CLASS =
            "com.android.car.carlauncher.CarLauncher";
    private static final String MAPS_PACKAGE = "com.google.android.apps.maps";


    @AssistedInject
    public HomeButtonController(@Assisted CarSystemBarButton homeButton,
            CarSystemBarElementStatusBarDisableController disableController,
            CarSystemBarElementStateController stateController,
            UserTracker userTracker, EventDispatcher eventDispatcher,
            ButtonSelectionStateController buttonSelectionStateController) {
        super(homeButton, disableController, stateController, userTracker, eventDispatcher,
                buttonSelectionStateController);

        homeButton.setVisibility(
                CarSystemUIUserUtil.isSecondaryMUMDSystemUI() ? View.GONE : View.VISIBLE);

        View.OnClickListener defaultClickListener = homeButton.getButtonClickListener();
        homeButton.setOnClickListener(view -> {
            boolean homeSurfaceActive = isHomeSurfaceActive(
                    homeButton,
                    userTracker.getUserId());
            Log.i(getClass().getSimpleName(),
                    "ARDA_HOME_SURFACE_ACTIVE=" + homeSurfaceActive
                            + " selected=" + homeButton.getSelected());
            if (!homeSurfaceActive) {
                defaultClickListener.onClick(view);
                return;
            }
            Intent toggleIntent = new Intent(ACTION_TOGGLE_MAP_FULLSCREEN)
                    .setPackage(CAR_LAUNCHER_PACKAGE);
            homeButton.getContext().sendBroadcastAsUser(
                    toggleIntent,
                    userTracker.getUserHandle());
        });
    }


    private static boolean isHomeSurfaceActive(View homeButton, int userId) {
        ActivityManager activityManager =
                homeButton.getContext().getSystemService(ActivityManager.class);
        if (activityManager == null) {
            return false;
        }

        List<ActivityManager.RunningTaskInfo> tasks =
                activityManager.getRunningTasks(50);
        boolean launcherVisible = false;
        boolean focusedTaskBelongsToHomeSurface = false;

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (task.userId != userId) {
                continue;
            }

            ComponentName component = task.topActivity != null
                    ? task.topActivity : task.baseActivity;
            String packageName = component != null
                    ? component.getPackageName() : null;
            boolean isLauncherHomeActivity = component != null
                    && CAR_LAUNCHER_PACKAGE.equals(packageName)
                    && CAR_LAUNCHER_HOME_CLASS.equals(component.getClassName());

            if (task.isVisible && isLauncherHomeActivity) {
                launcherVisible = true;
            }
            if (task.isFocused
                    && (isLauncherHomeActivity
                    || MAPS_PACKAGE.equals(packageName))) {
                focusedTaskBelongsToHomeSurface = true;
            }
        }

        return launcherVisible && focusedTaskBelongsToHomeSurface;
    }

    @AssistedFactory
    public interface Factory extends
            CarSystemBarElementController.Factory<CarSystemBarButton,
                    HomeButtonController> {
    }

    @Override
    protected boolean shouldBeVisible() {
        return !CarSystemUIUserUtil.isSecondaryMUMDSystemUI();
    }
}
