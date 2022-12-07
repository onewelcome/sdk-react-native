package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableArray
import com.onegini.mobile.sdk.android.handlers.OneginiImplicitAuthenticationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiImplicitTokenRequestError
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.PROFILE_DOES_NOT_EXIST
import com.onegini.mobile.sdk.reactnative.managers.AuthenticatorManager
import com.onegini.mobile.sdk.reactnative.mapers.ScopesMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticateUserImplicitlyUseCase @Inject constructor(
    private val oneginiSDK: OneginiSDK,
    private val authenticatorManager: AuthenticatorManager
) {
    operator fun invoke(profileId: String, scopes: ReadableArray?, promise: Promise) {
        authenticatorManager.getUserProfile(profileId)?.let { userProfile ->
            val scopesArray = ScopesMapper.toStringArray(scopes)
            oneginiSDK.oneginiClient.userClient
                .authenticateUserImplicitly(
                    userProfile, scopesArray,
                    object : OneginiImplicitAuthenticationHandler {
                        override fun onSuccess(profile: UserProfile) {
                            promise.resolve(null)
                        }

                        override fun onError(error: OneginiImplicitTokenRequestError) {
                            promise.reject(error.errorType.toString(), error.message)
                        }
                    }
                )
        } ?: promise.reject(PROFILE_DOES_NOT_EXIST.code.toString(), PROFILE_DOES_NOT_EXIST.message)
    }
}
