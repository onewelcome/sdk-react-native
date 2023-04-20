package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.mapers.UserProfileMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetUserProfilesUseCase @Inject constructor(private val oneginiSDK: OneginiSDK) {
    operator fun invoke(promise: Promise) {
        val profiles = oneginiSDK.oneginiClient.userClient.userProfiles
        promise.resolve(UserProfileMapper.toWritableMap(profiles))
    }
}
