package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.OneginiComponets
import com.onegini.mobile.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.android.handlers.OneginiImplicitAuthenticationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiImplicitTokenRequestError
import com.onegini.mobile.sdk.android.model.entity.UserProfile

class AuthenticateUserImplicitlyUseCase(val getUserProfileUseCase: GetUserProfileUseCase = GetUserProfileUseCase()) {

    operator fun invoke(profileId: String?, promise: Promise) {
        val userProfile = getUserProfileUseCase(profileId)

        if (userProfile == null) {
            promise.reject(OneginiWrapperErrors.USER_PROFILE_IS_NULL.code, OneginiWrapperErrors.USER_PROFILE_IS_NULL.message)
            return
        }

        // should it be passed as method's param?
        val scope = arrayOf("read")

        OneginiComponets.oneginiSDK.oneginiClient.userClient
            .authenticateUserImplicitly(
                userProfile, scope,
                object : OneginiImplicitAuthenticationHandler {
                    override fun onSuccess(profile: UserProfile) {
                        promise.resolve(null)
                    }

                    override fun onError(error: OneginiImplicitTokenRequestError) {
                        promise.reject(error.errorType.toString(), error.message)
                    }
                }
            )
    }
}
