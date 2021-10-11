package com.onegini.mobile.clean.use_cases

import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.sdk.android.model.entity.UserProfile

class GetUserProfileUseCase(private val oneginiSDK: OneginiSDK) {

    operator fun invoke(profileId: String?): UserProfile? {
        return oneginiSDK.oneginiClient.userClient.userProfiles
            .find { it.profileId == profileId }
    }
}
