package com.onegini.mobile.sdk.reactnative.handlers.fingerprint

import com.onegini.mobile.sdk.android.handlers.request.OneginiFingerprintAuthenticationRequestHandler
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiFingerprintCallback
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import javax.inject.Inject


class FingerprintAuthenticationRequestHandler : OneginiFingerprintAuthenticationRequestHandler {
    @Inject
    lateinit var eventEmitter: FingerprintAuthenticationEventEmitter

    private var callback: OneginiFingerprintCallback? = null

    override fun startAuthentication(user: UserProfile, callback: OneginiFingerprintCallback) {
        this.callback = callback
        eventEmitter.startAuthentication(user)
    }

    override fun onNextAuthenticationAttempt() {
        eventEmitter.onNextAuthenticationAttempt()
    }

    override fun onFingerprintCaptured() {
        eventEmitter.onFingerprintCaptured()
    }

    override fun finishAuthentication() {
        eventEmitter.finishAuthentication()
    }

    fun acceptAuthenticationRequest() {
        callback?.acceptAuthenticationRequest()
    }

    fun denyAuthenticationRequest() {
        callback?.denyAuthenticationRequest()
    }

    fun fallbackToPin() {
        callback?.fallbackToPin()
    }
}
