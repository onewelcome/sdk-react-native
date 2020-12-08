package com.onegini.mobile.view.handlers.fingerprint

import com.onegini.mobile.sdk.android.handlers.request.OneginiFingerprintAuthenticationRequestHandler
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiFingerprintCallback
import com.onegini.mobile.sdk.android.model.entity.UserProfile

class FingerprintAuthenticationRequestHandler : OneginiFingerprintAuthenticationRequestHandler {

    private var callback: OneginiFingerprintCallback? = null
    private var observer: FingerprintAuthenticationObserver? = null
    fun setPinNotificationObserver(observer: FingerprintAuthenticationObserver?) {
        this.observer = observer
    }

    override fun startAuthentication(user: UserProfile?, callback: OneginiFingerprintCallback) {
        this.callback = callback
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
}