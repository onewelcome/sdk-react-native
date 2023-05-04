package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticatorDeregistrationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiAuthenticatorDeregistrationError
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError.AUTHENTICATOR_DOES_NOT_EXIST
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError.AUTHENTICATOR_NOT_REGISTERED
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError.NO_PROFILE_AUTHENTICATED
import com.onegini.mobile.sdk.reactnative.exception.rejectOneginiException
import com.onegini.mobile.sdk.reactnative.exception.rejectWrapperError
import com.onegini.mobile.sdk.reactnative.managers.AuthenticatorManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeregisterAuthenticatorUseCase @Inject constructor(
  private val oneginiSDK: OneginiSDK,
  private val authenticatorManager: AuthenticatorManager
) {
  operator fun invoke(authenticatorId: String, promise: Promise) {
    val userProfile =
      oneginiSDK.oneginiClient.userClient.authenticatedUserProfile ?: return promise.rejectWrapperError(NO_PROFILE_AUTHENTICATED)

    val authenticator =
      authenticatorManager.getAuthenticator(userProfile, authenticatorId) ?: return promise.rejectWrapperError(AUTHENTICATOR_DOES_NOT_EXIST)

    if (!authenticator.isRegistered) {
      return promise.rejectWrapperError(AUTHENTICATOR_NOT_REGISTERED)
    }
    oneginiSDK.oneginiClient.userClient.deregisterAuthenticator(
      authenticator,
      object : OneginiAuthenticatorDeregistrationHandler {
        override fun onSuccess() {
          promise.resolve(null)
        }

        override fun onError(error: OneginiAuthenticatorDeregistrationError) {
          promise.rejectOneginiException(error)
        }
      })
  }
}
