package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.OneginiComponets
import com.onegini.mobile.exception.OneginiWrapperErrors
import com.onegini.mobile.mapers.CustomInfoMapper
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticatorRegistrationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiAuthenticatorRegistrationError
import com.onegini.mobile.sdk.android.model.OneginiAuthenticator
import com.onegini.mobile.sdk.android.model.entity.CustomInfo

class RegisterAuthenticatorUseCase(val getUserProfileUseCase: GetUserProfileUseCase = GetUserProfileUseCase()) {

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

        val notRegisteredAuthenticators: Set<OneginiAuthenticator> = OneginiComponets.oneginiSDK.oneginiClient.userClient.getNotRegisteredAuthenticators(userProfile)
        for (auth in notRegisteredAuthenticators) {
            if (auth.type == authType) {
                authenticator = auth
            }
        }

        if (authenticator == null) {
            promise.reject(OneginiWrapperErrors.AUTHENTICATED_USER_PROFILE_IS_NULL.code, OneginiWrapperErrors.AUTHENTICATED_USER_PROFILE_IS_NULL.message)
            return
        }

        OneginiComponets.oneginiSDK.oneginiClient.userClient.registerAuthenticator(
            authenticator,
            object : OneginiAuthenticatorRegistrationHandler {
                override fun onSuccess(info: CustomInfo?) {
                    promise.resolve(CustomInfoMapper.toWritableMap(info))
                }

                override fun onError(error: OneginiAuthenticatorRegistrationError?) {
                    promise.reject(error?.errorType.toString(), error?.message)
                }
            }
        )
    }
}
