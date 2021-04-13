package com.onegini.mobile.view.handlers.fingerprint

import com.onegini.mobile.sdk.android.model.entity.UserProfile

interface FingerprintAuthenticationObserver {

    fun startAuthentication(user: UserProfile?)

    fun onNextAuthenticationAttempt()

    fun onFingerprintCaptured()

    fun finishAuthentication()
}