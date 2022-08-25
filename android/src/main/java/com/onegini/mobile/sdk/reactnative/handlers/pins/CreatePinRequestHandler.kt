//@todo Later will be transferred to RN Wrapper
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
        eventEmitter.onPinOpen(PinFlow.Create, userProfile.profileId, pinLength)
    }

    override fun onNextPinCreationAttempt(oneginiPinValidationError: OneginiPinValidationError) {
        eventEmitter.onError(oneginiPinValidationError.errorType, oneginiPinValidationError.message ?: "", PinFlow.Create)
    }

    override fun finishPinCreation() {
        eventEmitter.onPinClose(PinFlow.Create)
    }

    fun onPinProvided(pin: CharArray) {
        pinCallback?.acceptAuthenticationRequest(pin)
    }

    fun pinCancelled() {
        pinCallback?.denyAuthenticationRequest()
    }
}
