package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.model.OneginiAuthenticator
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError.PROFILE_DOES_NOT_EXIST
import com.onegini.mobile.sdk.reactnative.exception.rejectWrapperError
import com.onegini.mobile.sdk.reactnative.mapers.OneginiAuthenticatorMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRegisteredAuthenticatorsUseCase @Inject constructor(
  private val oneginiSDK: OneginiSDK,
  private val getUserProfileUseCase: GetUserProfileUseCase
) {
  operator fun invoke(profileId: String, promise: Promise) {
    val userProfile = getUserProfileUseCase(profileId) ?: return promise.rejectWrapperError(PROFILE_DOES_NOT_EXIST)
    val authenticators = oneginiSDK.oneginiClient.userClient.getRegisteredAuthenticators(userProfile)
    promise.resolve(OneginiAuthenticatorMapper.toWritableMap(authenticators))
  }

  operator fun invoke(userProfile: UserProfile): Set<OneginiAuthenticator> {
    return oneginiSDK.oneginiClient.userClient.getRegisteredAuthenticators(userProfile)
  }
}
