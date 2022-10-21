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
        pinCallback = null
    }

    fun onPinProvided(pin: CharArray): Boolean {
        pinCallback?.let { callBack ->
            callBack.acceptAuthenticationRequest(pin)
            return true
        }
        return false
    }

    fun pinCancelled(): Boolean {
        pinCallback?.let { callBack ->
            callBack.denyAuthenticationRequest()
            return true
        }
        return false
    }
}
