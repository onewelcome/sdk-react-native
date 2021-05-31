package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.mapers.UserProfileMapper

class GetAuthenticatedUserProfileUseCase(private val oneginiSDK: OneginiSDK) {

    operator fun invoke(promise: Promise) {
        val profile = oneginiSDK.oneginiClient.userClient.authenticatedUserProfile
        promise.resolve(UserProfileMapper.toWritableMap(profile))
    }
}
