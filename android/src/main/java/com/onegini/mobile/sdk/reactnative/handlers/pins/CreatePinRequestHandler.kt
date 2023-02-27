package com.onegini.mobile.sdk.reactnative.handlers.pins

import com.onegini.mobile.sdk.android.handlers.error.OneginiPinValidationError
import com.onegini.mobile.sdk.android.handlers.request.OneginiCreatePinRequestHandler
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiPinCallback
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError.PIN_CREATION_NOT_IN_PROGRESS
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreatePinRequestHandler @Inject constructor(private val eventEmitter: CreatePinEventEmitter):
    OneginiCreatePinRequestHandler {

    private var pinCallback: OneginiPinCallback? = null

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

    fun onPinProvided(pin: CharArray) {
        pinCallback?.acceptAuthenticationRequest(pin) ?: throw OneginiReactNativeException(PIN_CREATION_NOT_IN_PROGRESS)
    }

    fun cancelPin() {
        pinCallback?.denyAuthenticationRequest() ?: throw OneginiReactNativeException(PIN_CREATION_NOT_IN_PROGRESS)
    }
}
