package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.OneginiComponets
import com.onegini.mobile.exception.OneginiWrapperErrors
import com.onegini.mobile.mapers.OneginiAuthenticatorMapper

class GetRegisteredAuthenticatorsUseCase(val getUserProfileUseCase: GetUserProfileUseCase = GetUserProfileUseCase()) {

    operator fun invoke(profileId: String, promise: Promise) {
        val userProfile = getUserProfileUseCase(profileId)

        if (userProfile == null) {
            promise.reject(OneginiWrapperErrors.USER_PROFILE_IS_NULL.code, OneginiWrapperErrors.USER_PROFILE_IS_NULL.message)
            return
        }

        val authenticators = OneginiComponets.oneginiSDK.oneginiClient.userClient.getRegisteredAuthenticators(userProfile)

        promise.resolve(OneginiAuthenticatorMapper.toWritableMap(authenticators))
    }
}
