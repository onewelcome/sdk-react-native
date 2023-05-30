package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.rejectRNException
import com.onegini.mobile.sdk.reactnative.handlers.registration.RegistrationRequestHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CancelBrowserRegistrationUseCase @Inject constructor(private val registrationRequestHandler: RegistrationRequestHandler) {
  operator fun invoke(promise: Promise) {
    try {
      registrationRequestHandler.cancelRegistration()
      return promise.resolve(null)
    } catch (exception: OneginiReactNativeException) {
      promise.rejectRNException(exception)
    }
  }
}
