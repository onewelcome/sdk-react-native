package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableArray
import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.exception.OneginiWrapperErrors
import com.onegini.mobile.mapers.RegistrationScopesMapper
import com.onegini.mobile.sdk.android.handlers.OneginiImplicitAuthenticationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiImplicitTokenRequestError
import com.onegini.mobile.sdk.android.model.entity.UserProfile

class AuthenticateUserImplicitlyUseCase(private val oneginiSDK: OneginiSDK, val getUserProfileUseCase: GetUserProfileUseCase = GetUserProfileUseCase(oneginiSDK)) {

    operator fun invoke(profileId: String?, scopes: ReadableArray, promise: Promise) {
        val userProfile = getUserProfileUseCase(profileId)

        if (userProfile == null) {
            promise.reject(OneginiWrapperErrors.USER_PROFILE_IS_NULL.code, OneginiWrapperErrors.USER_PROFILE_IS_NULL.message)
            return
        }

        val scopesArray = RegistrationScopesMapper.toStringArray(scopes)

        oneginiSDK.oneginiClient.userClient
            .authenticateUserImplicitly(
                userProfile, scopesArray,
                object : OneginiImplicitAuthenticationHandler {
                    override fun onSuccess(profile: UserProfile) {
                        promise.resolve(profile)
                    }

                    override fun onError(error: OneginiImplicitTokenRequestError) {
                        promise.reject(error.errorType.toString(), error.message)
                    }
                }
            )
    }
}
