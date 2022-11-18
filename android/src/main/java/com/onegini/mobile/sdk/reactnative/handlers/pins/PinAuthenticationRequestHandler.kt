package com.onegini.mobile.sdk.reactnative.handlers.pins

import com.onegini.mobile.sdk.android.handlers.request.OneginiPinAuthenticationRequestHandler
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiPinCallback
import com.onegini.mobile.sdk.android.model.entity.AuthenticationAttemptCounter
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors
import javax.inject.Inject

class PinAuthenticationRequestHandler : OneginiPinAuthenticationRequestHandler {
    @Inject
    lateinit var eventEmitter: PinAuthenticationEventEmitter

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
        eventEmitter.onPinClose()
    }

    @Throws(OneginiReactNativeException::class)
    fun acceptAuthenticationRequest(pin: CharArray) {
        callback?.let { pinCallback ->
            pinCallback.acceptAuthenticationRequest(pin)
            callback = null
        } ?: throw OneginiReactNativeException(OneginiWrapperErrors.AUTHENTICATION_NOT_IN_PROGRESS.code.toInt(), OneginiWrapperErrors.AUTHENTICATION_NOT_IN_PROGRESS.message)
    }

    fun denyAuthenticationRequest() {
        callback?.denyAuthenticationRequest()
    }
}
