package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.OneginiComponets
import com.onegini.mobile.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticatorDeregistrationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiAuthenticatorDeregistrationError
import com.onegini.mobile.sdk.android.model.OneginiAuthenticator

class DeregisterAuthenticatorUseCase(val getUserProfileUseCase: GetUserProfileUseCase = GetUserProfileUseCase()) {

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

        val registeredAuthenticators: Set<OneginiAuthenticator> = OneginiComponets.oneginiSDK.oneginiClient.userClient.getRegisteredAuthenticators(userProfile)
        for (auth in registeredAuthenticators) {
            if (auth.type == authType) {
                authenticator = auth
            }
        }

        if (authenticator == null) {
            promise.reject(OneginiWrapperErrors.AUTHENTICATED_USER_PROFILE_IS_NULL.code, OneginiWrapperErrors.AUTHENTICATED_USER_PROFILE_IS_NULL.message)
            return
        }

        OneginiComponets.oneginiSDK.oneginiClient.userClient.deregisterAuthenticator(
            authenticator,
            object : OneginiAuthenticatorDeregistrationHandler {
                override fun onSuccess() {
                    promise.resolve(null)
                }

                override fun onError(error: OneginiAuthenticatorDeregistrationError?) {
                    promise.reject(error?.errorType.toString(), error?.message)
                }
            }
        )
    }
}
