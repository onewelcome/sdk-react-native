package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.mapers.UserProfileMapper

class GetAuthenticatedUserProfileUseCase(private val oneginiSDK: OneginiSDK) {

    operator fun invoke(promise: Promise) {
        val profile = oneginiSDK.oneginiClient.userClient.authenticatedUserProfile
        promise.resolve(UserProfileMapper.toWritableMap(profile))
    }
}
