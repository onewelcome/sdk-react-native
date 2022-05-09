package com.onegini.mobile.sdk.reactnative.handlers.fingerprint

import com.onegini.mobile.sdk.android.handlers.request.OneginiFingerprintAuthenticationRequestHandler
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiFingerprintCallback
import com.onegini.mobile.sdk.android.model.entity.UserProfile

class FingerprintAuthenticationRequestHandler : OneginiFingerprintAuthenticationRequestHandler {

    private var callback: OneginiFingerprintCallback? = null
    var observer: FingerprintAuthenticationObserver? = null

    override fun startAuthentication(user: UserProfile, callback: OneginiFingerprintCallback) {
        this.callback = callback
        observer?.startAuthentication(user)
    }

    override fun onNextAuthenticationAttempt() {
        observer?.onNextAuthenticationAttempt()
    }

    override fun onFingerprintCaptured() {
        observer?.onFingerprintCaptured()
    }

    override fun finishAuthentication() {
        observer?.finishAuthentication()
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
