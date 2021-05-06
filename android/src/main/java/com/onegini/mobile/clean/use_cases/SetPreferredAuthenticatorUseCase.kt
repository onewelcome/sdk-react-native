package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.OneginiComponets
import com.onegini.mobile.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.android.model.OneginiAuthenticator

class SetPreferredAuthenticatorUseCase(val getUserProfileUseCase: GetUserProfileUseCase = GetUserProfileUseCase()) {

    operator fun invoke(profileId: String, idOneginiAuthenticator: String, promise: Promise) {
        val userProfile = getUserProfileUseCase(profileId)

        if (userProfile == null) {
            promise.reject(OneginiWrapperErrors.USER_PROFILE_IS_NULL.code, OneginiWrapperErrors.USER_PROFILE_IS_NULL.message)
            return
        }

        var authenticator: OneginiAuthenticator? = null

        val registeredAuthenticators: Set<OneginiAuthenticator> = OneginiComponets.oneginiSDK.oneginiClient.userClient.getRegisteredAuthenticators(userProfile)
        for (auth in registeredAuthenticators) {
            if (auth.id == idOneginiAuthenticator) {
                authenticator = auth
            }
        }

        if (authenticator == null) {
            promise.reject(OneginiWrapperErrors.AUTHENTICATED_USER_PROFILE_IS_NULL.code, OneginiWrapperErrors.AUTHENTICATED_USER_PROFILE_IS_NULL.message)
            return
        }

        OneginiComponets.oneginiSDK.oneginiClient.userClient.setPreferredAuthenticator(
            authenticator
        )

        promise.resolve(null)
    }
}
