package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiAuthenticationError
import com.onegini.mobile.sdk.android.model.entity.CustomInfo
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError.PROFILE_DOES_NOT_EXIST
import com.onegini.mobile.sdk.reactnative.exception.rejectOneginiException
import com.onegini.mobile.sdk.reactnative.exception.rejectWrapperError
import com.onegini.mobile.sdk.reactnative.mapers.CustomInfoMapper
import com.onegini.mobile.sdk.reactnative.mapers.UserProfileMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticateUserUseCase @Inject constructor(
  private val oneginiSDK: OneginiSDK,
  private val getRegisteredAuthenticatorsUseCase: GetRegisteredAuthenticatorsUseCase,
  private val getUserProfileUseCase: GetUserProfileUseCase
) {
  operator fun invoke(profileId: String, authenticatorId: String?, promise: Promise) {
    val userProfile = getUserProfileUseCase(profileId) ?: return promise.rejectWrapperError(PROFILE_DOES_NOT_EXIST)

    val handler = object : OneginiAuthenticationHandler {
      override fun onSuccess(userProfile: UserProfile, customInfo: CustomInfo?) {
        val result = Arguments.createMap()
        UserProfileMapper.add(result, userProfile)
        CustomInfoMapper.add(result, customInfo)
        promise.resolve(result)
      }

      override fun onError(error: OneginiAuthenticationError) {
        promise.rejectOneginiException(error)
      }
    }

    getRegisteredAuthenticatorsUseCase(userProfile)
      .find { it.id == authenticatorId }
      ?.let { authenticator ->
        oneginiSDK.oneginiClient.userClient.authenticateUser(
          userProfile,
          authenticator,
          handler
        )
      } ?: oneginiSDK.oneginiClient.userClient.authenticateUser(userProfile, handler)
  }
}
