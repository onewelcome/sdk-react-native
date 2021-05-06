package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.OneginiComponets
import com.onegini.mobile.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.android.model.OneginiAuthenticator

class IsAuthenticatorRegisteredUseCase(val getUserProfileUseCase: GetUserProfileUseCase = GetUserProfileUseCase()) {

    operator fun invoke(profileId: String, type: String, promise: Promise) {
        val userProfile = getUserProfileUseCase(profileId)

        if (userProfile == null) {
            promise.reject(OneginiWrapperErrors.USER_PROFILE_IS_NULL.code, OneginiWrapperErrors.USER_PROFILE_IS_NULL.message)
            return
        }

        val authType = when (type) {
            "Fingerprint" -> OneginiAuthenticator.FINGERPRINT
            "Pin" -> OneginiAuthenticator.PIN
            "Custom" -> OneginiAuthenticator.CUSTOM
            else -> OneginiAuthenticator.CUSTOM
        }

        var authenticator: OneginiAuthenticator? = null

        val notRegisteredAuthenticators: Set<OneginiAuthenticator> = OneginiComponets.oneginiSDK.oneginiClient.userClient.getRegisteredAuthenticators(userProfile)
        for (auth in notRegisteredAuthenticators) {
            if (auth.type == authType) {
                authenticator = auth
            }
        }

        promise.resolve(authenticator != null)
    }
}
