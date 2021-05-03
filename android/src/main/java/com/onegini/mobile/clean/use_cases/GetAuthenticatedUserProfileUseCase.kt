package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.OneginiComponets
import com.onegini.mobile.mapers.OneginiIdentityProviderMapper
import com.onegini.mobile.mapers.UserProfileMapper

class GetAuthenticatedUserProfileUseCase {

    operator fun invoke(promise: Promise) {
        val profile = OneginiComponets.oneginiSDK.oneginiClient.userClient.authenticatedUserProfile
        promise.resolve(UserProfileMapper.toWritableMap(profile))
    }
}