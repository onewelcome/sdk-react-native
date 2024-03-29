package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticatorRegistrationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiAuthenticatorRegistrationError
import com.onegini.mobile.sdk.android.model.entity.CustomInfo
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError.AUTHENTICATOR_DOES_NOT_EXIST
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError.NO_PROFILE_AUTHENTICATED
import com.onegini.mobile.sdk.reactnative.exception.rejectOneginiException
import com.onegini.mobile.sdk.reactnative.exception.rejectWrapperError
import com.onegini.mobile.sdk.reactnative.managers.AuthenticatorManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegisterAuthenticatorUseCase @Inject constructor(
  private val oneginiSDK: OneginiSDK,
  private val authenticatorManager: AuthenticatorManager
) {
  operator fun invoke(authenticatorId: String, promise: Promise) {
    val userProfile =
      oneginiSDK.oneginiClient.userClient.authenticatedUserProfile ?: return promise.rejectWrapperError(NO_PROFILE_AUTHENTICATED)

    val authenticator =
      authenticatorManager.getAuthenticator(userProfile, authenticatorId) ?: return promise.rejectWrapperError(AUTHENTICATOR_DOES_NOT_EXIST)

    // We don't have to check if the authenticator is already registered as the sdk will do that for us.
    oneginiSDK.oneginiClient.userClient.registerAuthenticator(authenticator, object : OneginiAuthenticatorRegistrationHandler {
      override fun onSuccess(customInfo: CustomInfo?) {
        promise.resolve(null)
      }

      override fun onError(error: OneginiAuthenticatorRegistrationError) {
        promise.rejectOneginiException(error)
      }
    })
  }
}
