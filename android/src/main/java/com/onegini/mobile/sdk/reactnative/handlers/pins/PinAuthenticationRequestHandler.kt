package com.onegini.mobile.sdk.reactnative.handlers.pins

import com.onegini.mobile.sdk.android.handlers.request.OneginiPinAuthenticationRequestHandler
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiPinCallback
import com.onegini.mobile.sdk.android.model.entity.AuthenticationAttemptCounter
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.AUTHENTICATION_NOT_IN_PROGRESS
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PinAuthenticationRequestHandler @Inject constructor(private val eventEmitter: PinAuthenticationEventEmitter):
    OneginiPinAuthenticationRequestHandler {

    private var callback: OneginiPinCallback? = null

    override fun startAuthentication(
        userProfile: UserProfile,
        oneginiPinCallback: OneginiPinCallback,
        attemptCounter: AuthenticationAttemptCounter
    ) {
        callback = oneginiPinCallback
        eventEmitter.onPinOpen(userProfile.profileId)
    }

    override fun onNextAuthenticationAttempt(attemptCounter: AuthenticationAttemptCounter) {
        eventEmitter.onIncorrectPin(attemptCounter.remainingAttempts)
    }

    override fun finishAuthentication() {
        callback = null
        eventEmitter.onPinClose()
    }

    fun acceptAuthenticationRequest(pin: CharArray) {
        callback?.acceptAuthenticationRequest(pin) ?: throw OneginiReactNativeException(
            AUTHENTICATION_NOT_IN_PROGRESS.code,
            AUTHENTICATION_NOT_IN_PROGRESS.message
        )
    }

    fun denyAuthenticationRequest() {
        callback?.denyAuthenticationRequest() ?: throw OneginiReactNativeException(
            AUTHENTICATION_NOT_IN_PROGRESS.code,
            AUTHENTICATION_NOT_IN_PROGRESS.message
        )
        callback = null
    }
}
