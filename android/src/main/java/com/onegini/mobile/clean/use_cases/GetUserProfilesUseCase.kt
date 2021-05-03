package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.OneginiComponets
import com.onegini.mobile.mapers.UserProfileMapper

class GetUserProfilesUseCase {

    operator fun invoke(promise: Promise) {
        val profiles = OneginiComponets.oneginiSDK.oneginiClient.userClient.userProfiles
        promise.resolve(UserProfileMapper.toWritableMap(profiles))
    }
}
