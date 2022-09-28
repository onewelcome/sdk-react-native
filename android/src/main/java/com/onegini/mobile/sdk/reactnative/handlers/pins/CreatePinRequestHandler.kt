package com.onegini.mobile.sdk.reactnative.handlers.pins

import com.onegini.mobile.sdk.android.handlers.request.OneginiCreatePinRequestHandler
import com.onegini.mobile.sdk.reactnative.Constants.PinFlow
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiPinCallback
import com.onegini.mobile.sdk.android.handlers.error.OneginiPinValidationError

class CreatePinRequestHandler : OneginiCreatePinRequestHandler {
    private var pinCallback: OneginiPinCallback? = null
    private var eventEmitter = CreatePinEventEmitter()

    override fun startPinCreation(
        userProfile: UserProfile,
        oneginiPinCallback: OneginiPinCallback,
        pinLength: Int
    ) {
        pinCallback = oneginiPinCallback
        eventEmitter.onPinOpen(userProfile.profileId, pinLength)
    }

    override fun onNextPinCreationAttempt(oneginiPinValidationError: OneginiPinValidationError) {
        eventEmitter.onPinNotAllowed(oneginiPinValidationError.errorType, oneginiPinValidationError.message ?: "")
    }

    override fun finishPinCreation() {
        eventEmitter.onPinClose()
    }

    fun onPinProvided(pin: CharArray) {
        pinCallback?.acceptAuthenticationRequest(pin)
    }

    fun pinCancelled() {
        pinCallback?.denyAuthenticationRequest()
    }
}
