package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetUserProfileUseCase @Inject constructor(private val oneginiSDK: OneginiSDK) {

    operator fun invoke(profileId: String): UserProfile? {
        return oneginiSDK.oneginiClient.userClient.userProfiles
            .find { it.profileId == profileId }
    }
}
