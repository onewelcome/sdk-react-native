package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError.ACTION_NOT_ALLOWED
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError.IDENTITY_PROVIDER_NOT_FOUND
import com.onegini.mobile.sdk.reactnative.exception.SUBMIT_CUSTOM_REGISTRATION_ACTION_NOT_ALLOWED
import com.onegini.mobile.sdk.reactnative.exception.rejectWrapperError
import com.onegini.mobile.sdk.reactnative.handlers.customregistration.CustomRegistrationAction
import com.onegini.mobile.sdk.reactnative.managers.CustomRegistrationActionManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubmitCustomRegistrationActionUseCase @Inject constructor(private val customRegistrationActionManager: CustomRegistrationActionManager) {
  operator fun invoke(identityProviderId: String, token: String?, promise: Promise) {
    customRegistrationActionManager.getCustomRegistrationAction(identityProviderId)?.let { action ->
      tryReturnSuccess(action, token, promise)
    } ?: promise.rejectWrapperError(IDENTITY_PROVIDER_NOT_FOUND)
  }

  private fun tryReturnSuccess(
    action: CustomRegistrationAction,
    token: String?,
    promise: Promise
  ) {
    try {
      action.returnSuccess(token)
      promise.resolve(null)
    } catch (exception: OneginiReactNativeException) {
      promise.reject(ACTION_NOT_ALLOWED.code.toString(), SUBMIT_CUSTOM_REGISTRATION_ACTION_NOT_ALLOWED)
    }
  }
}
