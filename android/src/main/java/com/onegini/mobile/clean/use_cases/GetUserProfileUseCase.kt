package com.onegini.mobile.clean.use_cases

import com.onegini.mobile.OneginiComponets
import com.onegini.mobile.sdk.android.model.entity.UserProfile

class GetUserProfileUseCase {

    operator fun invoke(profileId: String?): UserProfile? {
        if (profileId == null) {
            return null
        }
        OneginiComponets.oneginiSDK.oneginiClient.userClient.userProfiles.forEach {
            if (it.profileId == profileId) {
                return it
            }
        }
        return null
    }
}
