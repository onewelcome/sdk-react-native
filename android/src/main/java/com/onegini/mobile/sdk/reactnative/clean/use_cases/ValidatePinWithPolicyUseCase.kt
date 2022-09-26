package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.android.handlers.OneginiPinValidationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiPinValidationError
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors


class ValidatePinWithPolicyUseCase(private val oneginiSDK: OneginiSDK) {
  operator fun invoke(pin: String?, promise: Promise) {
    if (pin == null) {
      promise.reject(OneginiWrapperErrors.PARAMETERS_NOT_CORRECT.code, "Expected parameter 'pin' to be String but was NULL")
      return
    }
    oneginiSDK.oneginiClient.userClient.validatePinWithPolicy(pin.toCharArray(), object: OneginiPinValidationHandler {
      override fun onSuccess() {
        promise.resolve(null);
      }

      override fun onError(err: OneginiPinValidationError) {
        promise.reject(err.errorType.toString(), err.message)
      }
    })
  }
}
