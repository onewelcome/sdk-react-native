package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.exception.OneginiWrapperErrors
import com.onegini.mobile.mapers.OneginiAuthenticatorMapper
import com.onegini.mobile.sdk.android.model.OneginiAuthenticator
import com.onegini.mobile.sdk.android.model.entity.UserProfile

class GetRegisteredAuthenticatorsUseCase(
    private val oneginiSDK: OneginiSDK,
    private val getUserProfileUseCase: GetUserProfileUseCase = GetUserProfileUseCase(oneginiSDK)
) {
    operator fun invoke(profileId: String, promise: Promise) {
        val userProfile = getUserProfileUseCase(profileId)

        if (userProfile == null) {
            promise.reject(OneginiWrapperErrors.USER_PROFILE_IS_NULL.code, OneginiWrapperErrors.USER_PROFILE_IS_NULL.message)
            return
        }

        val authenticators = getList(userProfile)

        promise.resolve(OneginiAuthenticatorMapper.toWritableMap(authenticators))
    }

    fun getList(userProfile: UserProfile): Set<OneginiAuthenticator> {
        return oneginiSDK.oneginiClient.userClient.getRegisteredAuthenticators(userProfile)
    }
}
