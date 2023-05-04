package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.OneginiMobileAuthEnrollmentHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiMobileAuthEnrollmentError
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.rejectOneginiException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class EnrollMobileAuthenticationUseCase @Inject constructor(private val oneginiSDK: OneginiSDK) {
  operator fun invoke(promise: Promise) {
    oneginiSDK.oneginiClient.userClient.enrollUserForMobileAuth(object : OneginiMobileAuthEnrollmentHandler {
      override fun onSuccess() {
        promise.resolve(null)
      }

      override fun onError(error: OneginiMobileAuthEnrollmentError) {
        promise.rejectOneginiException(error)
      }
    })
  }
}
