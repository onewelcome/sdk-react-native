//@todo Later will be transferred to RN Wrapper
package com.onegini.mobile.sdk.reactnative.handlers.pins;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.onegini.mobile.sdk.reactnative.Constants;
import com.onegini.mobile.sdk.reactnative.OneginiSDK;
import com.onegini.mobile.sdk.android.handlers.error.OneginiPinValidationError;
import com.onegini.mobile.sdk.android.handlers.request.OneginiCreatePinRequestHandler;
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiPinCallback;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;
import com.onegini.mobile.sdk.reactnative.util.PreferencesUtils;

import static com.onegini.mobile.sdk.reactnative.Constants.PIN_NOTIFICATION_CLOSE_VIEW;

public class CreatePinRequestHandler implements OneginiCreatePinRequestHandler {

    @NonNull
    private OneginiSDK oneginiSDK;

    @NonNull
    private Context context;

    @Nullable
    private PinWithConfirmationHandler pinWithConfirmationHandler;

    @Nullable
    private PinNotificationObserver pinNotificationHandler;

    private Constants.PinFlow lastPinFlow = Constants.PinFlow.Create;

    public CreatePinRequestHandler(@NonNull Context context, @NonNull OneginiSDK oneginiSDK) {
        this.context = context;
        this.oneginiSDK = oneginiSDK;
    }

    public void setPinNotificationObserver(@Nullable PinNotificationObserver pinNotificationHandler) {
        this.pinNotificationHandler = pinNotificationHandler;
        if (pinWithConfirmationHandler != null) {
            pinWithConfirmationHandler.setPinNotificationObserver(pinNotificationHandler);
        }
    }

    @Override
    public void startPinCreation(final UserProfile userProfile, final OneginiPinCallback oneginiPinCallback, final int pinLength) {
        pinWithConfirmationHandler = new PinWithConfirmationHandler(oneginiPinCallback, oneginiSDK, context);
        pinWithConfirmationHandler.setLastFlow(lastPinFlow);
        pinWithConfirmationHandler.setPinNotificationObserver(pinNotificationHandler);
        pinWithConfirmationHandler.notifyOnOpen(pinLength);
        PreferencesUtils.setPinLength(userProfile.getProfileId(), pinLength);
    }

    @Override
    public void onNextPinCreationAttempt(final OneginiPinValidationError oneginiPinValidationError) {
        if (pinWithConfirmationHandler != null) {
            pinWithConfirmationHandler.handlePinValidationError(oneginiPinValidationError);
        }
    }

    @Override
    public void finishPinCreation() {
        if (pinWithConfirmationHandler != null) {
            pinWithConfirmationHandler.notifyOnSimpleAction(PIN_NOTIFICATION_CLOSE_VIEW);
        }
    }

    void setPinFlow(Constants.PinFlow flow) {
        lastPinFlow = flow;
        if (pinWithConfirmationHandler != null) {
            pinWithConfirmationHandler.setLastFlow(flow);
        }
    }

    public void onPinProvided(final char[] pin, Constants.PinFlow flow) {
        if (pinWithConfirmationHandler != null) {
            pinWithConfirmationHandler.onPinProvided(pin, flow);
        }
    }

    public void pinCancelled(Constants.PinFlow flow) {
        if (pinWithConfirmationHandler != null) {
            pinWithConfirmationHandler.pinCancelled(flow);
        }
    }
}
