package com.onegini.mobile.sdk.reactnative.handlers.fingerprint

import com.onegini.mobile.sdk.android.handlers.request.OneginiFingerprintAuthenticationRequestHandler
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiFingerprintCallback
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FingerprintAuthenticationRequestHandler @Inject constructor(private val eventEmitter: FingerprintAuthenticationEventEmitter) :
  OneginiFingerprintAuthenticationRequestHandler {

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
    //What if callback is null?
    callback?.acceptAuthenticationRequest()
  }

  fun denyAuthenticationRequest() {
    //What if callback is null?
    callback?.denyAuthenticationRequest()
  }

  fun fallbackToPin() {
    //What if callback is null?
    callback?.fallbackToPin()
  }
}
