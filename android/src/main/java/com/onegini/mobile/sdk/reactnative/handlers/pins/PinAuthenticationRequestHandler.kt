package com.onegini.mobile.sdk.reactnative.handlers.pins

import com.onegini.mobile.sdk.android.handlers.request.OneginiPinAuthenticationRequestHandler
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiPinCallback
import com.onegini.mobile.sdk.android.model.entity.AuthenticationAttemptCounter
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.Constants

class PinAuthenticationRequestHandler : OneginiPinAuthenticationRequestHandler {
    private var callback: OneginiPinCallback? = null
    private var eventEmitter = PinAuthenticationEventEmitter()

    override fun startAuthentication(
        userProfile: UserProfile,
        oneginiPinCallback: OneginiPinCallback,
        attemptCounter: AuthenticationAttemptCounter
    ) {
        callback = oneginiPinCallback
        eventEmitter.onPinOpen(Constants.PinFlow.Authentication, userProfile.profileId)
    }

    override fun onNextAuthenticationAttempt(attemptCounter: AuthenticationAttemptCounter) {
        eventEmitter.onWrongPin(attemptCounter.remainingAttempts)
    }

    override fun finishAuthentication() {
        eventEmitter.onPinClose(Constants.PinFlow.Authentication)
    }

    fun acceptAuthenticationRequest(pin: CharArray) {
        callback?.acceptAuthenticationRequest(pin)
    }

    fun denyAuthenticationRequest() {
        callback?.denyAuthenticationRequest()
    }
}
