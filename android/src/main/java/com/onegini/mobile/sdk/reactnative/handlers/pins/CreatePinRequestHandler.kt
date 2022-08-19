//@todo Later will be transferred to RN Wrapper
package com.onegini.mobile.sdk.reactnative.handlers.pins

import com.onegini.mobile.sdk.android.handlers.request.OneginiCreatePinRequestHandler
import com.onegini.mobile.sdk.reactnative.Constants.PinFlow
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiPinCallback
import com.onegini.mobile.sdk.android.handlers.error.OneginiPinValidationError
import com.onegini.mobile.sdk.reactnative.Constants
import com.onegini.mobile.sdk.reactnative.Constants.PIN_NOTIFICATION_CLOSE_VIEW

class CreatePinRequestHandler : OneginiCreatePinRequestHandler {
    private var pinCallback: OneginiPinCallback? = null


    private var pinNotificationHandler: PinNotificationObserver? = null
    private var lastPinFlow: PinFlow = PinFlow.Create

    fun setPinNotificationObserver(pinNotificationHandler: PinNotificationObserver?) {
        this.pinNotificationHandler = pinNotificationHandler
    }

    override fun startPinCreation(
        userProfile: UserProfile,
        oneginiPinCallback: OneginiPinCallback,
        pinLength: Int
    ) {
        pinCallback = oneginiPinCallback
        notifyOnOpen(userProfile.profileId, pinLength)
    }

    override fun onNextPinCreationAttempt(oneginiPinValidationError: OneginiPinValidationError) {
            handlePinValidationError(oneginiPinValidationError)
    }

    override fun finishPinCreation() {
        notifyOnSimpleAction(PIN_NOTIFICATION_CLOSE_VIEW)
    }

    fun setPinFlow(flow: PinFlow) {
        lastPinFlow = flow
    }

    fun onPinProvided(pin: CharArray?, flow: PinFlow) {
        lastPinFlow = flow
        pinCallback?.acceptAuthenticationRequest(pin)
    }

    fun pinCancelled(flow: PinFlow) {
        lastPinFlow = flow
        pinCallback?.denyAuthenticationRequest()
    }


    fun handlePinValidationError(error: OneginiPinValidationError) {
        pinNotificationHandler?.onError(error.getErrorType(), error.message ?: "", lastPinFlow)
    }

    fun notifyOnSimpleAction(notifyAction: String) {
        pinNotificationHandler?.onNotify(notifyAction, lastPinFlow, null, null)
    }

    fun notifyOnOpen(profileId: String? = null, data: Any? = null) {
        pinNotificationHandler?.onNotify(Constants.PIN_NOTIFICATION_OPEN_VIEW, lastPinFlow, profileId, data)
    }
}
