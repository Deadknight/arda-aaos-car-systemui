/*
 * Copyright (C) 2026 The Android Open Source Project
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

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;

import com.android.systemui.car.systembar.element.CarSystemBarElementController;
import com.android.systemui.car.systembar.element.CarSystemBarElementStateController;
import com.android.systemui.car.systembar.element.CarSystemBarElementStatusBarDisableController;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

/** Injects a real system Back key event from the top car system bar. */
public class BackButtonController extends
        CarSystemBarElementController<CarSystemBarButton> {
    private static final String TAG = "ArdaBackButton";

    private final InputManager mInputManager;

    @AssistedInject
    public BackButtonController(@Assisted CarSystemBarButton backButton,
            CarSystemBarElementStatusBarDisableController disableController,
            CarSystemBarElementStateController stateController,
            Context context) {
        super(backButton, disableController, stateController);
        mInputManager = context.getSystemService(InputManager.class);
        backButton.setOnClickListener(this::onBackClick);
    }

    @AssistedFactory
    public interface Factory extends
            CarSystemBarElementController.Factory<CarSystemBarButton,
                    BackButtonController> {
    }

    private void onBackClick(View view) {
        if (mInputManager == null) {
            Log.e(TAG, "ARDA_SYSUI_BACK_INJECT=FAIL reason=input_manager_unavailable");
            return;
        }

        int displayId = view.getDisplay() != null
                ? view.getDisplay().getDisplayId()
                : Display.DEFAULT_DISPLAY;
        long eventTime = SystemClock.uptimeMillis();
        boolean downInjected = injectKeyEvent(
                KeyEvent.ACTION_DOWN, displayId, eventTime - 1);
        boolean upInjected = injectKeyEvent(
                KeyEvent.ACTION_UP, displayId, eventTime);

        if (downInjected && upInjected) {
            Log.i(TAG, "ARDA_SYSUI_BACK_INJECT=PASS display=" + displayId);
        } else {
            Log.e(TAG, "ARDA_SYSUI_BACK_INJECT=FAIL display=" + displayId
                    + " down=" + downInjected + " up=" + upInjected);
        }
    }

    private boolean injectKeyEvent(int action, int displayId, long eventTime) {
        KeyEvent event = new KeyEvent(
                eventTime,
                eventTime,
                action,
                KeyEvent.KEYCODE_BACK,
                /* repeat= */ 0,
                /* metaState= */ 0,
                KeyCharacterMap.VIRTUAL_KEYBOARD,
                /* scanCode= */ 0,
                KeyEvent.FLAG_FROM_SYSTEM | KeyEvent.FLAG_VIRTUAL_HARD_KEY,
                InputDevice.SOURCE_KEYBOARD);
        event.setDisplayId(displayId);
        return mInputManager.injectInputEvent(
                event,
                InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
    }
}
