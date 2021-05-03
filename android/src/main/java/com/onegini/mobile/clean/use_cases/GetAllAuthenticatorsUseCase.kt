package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.OneginiComponets
import com.onegini.mobile.exception.OneginiWrapperErrors
import com.onegini.mobile.mapers.OneginiAuthenticatorMapper
import com.onegini.mobile.sdk.android.model.entity.UserProfile

class GetAllAuthenticatorsUseCase {

    operator fun invoke(profileId: String, promise: Promise) {
        val userProfile = getUserProfile(profileId)

        if (userProfile == null) {
            promise.reject(OneginiWrapperErrors.USER_PROFILE_IS_NULL.code, OneginiWrapperErrors.USER_PROFILE_IS_NULL.message)
            return
        }

        val authenticators = OneginiComponets.oneginiSDK.oneginiClient.userClient.getAllAuthenticators(userProfile)

        promise.resolve(OneginiAuthenticatorMapper.toWritableMap(authenticators))
    }

    private fun getUserProfile(profileId: String?): UserProfile? {
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
