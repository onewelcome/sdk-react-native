package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableArray
import com.onegini.mobile.sdk.android.handlers.OneginiRegistrationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiRegistrationError
import com.onegini.mobile.sdk.android.model.OneginiIdentityProvider
import com.onegini.mobile.sdk.android.model.entity.CustomInfo
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.reactnative.mapers.ScopesMapper
import com.onegini.mobile.sdk.reactnative.mapers.UserProfileMapper

class RegisterUserUseCase(private val oneginiSDK: OneginiSDK) {

    operator fun invoke(identityProviderId: String?, scopes: ReadableArray, promise: Promise) {
        val identityProvider = getIdentityProvider(identityProviderId)

        if (identityProvider == null && identityProviderId != null) {
            promise.reject(OneginiWrapperErrors.IDENTITY_PROVIDER_NOT_FOUND.code, OneginiWrapperErrors.IDENTITY_PROVIDER_NOT_FOUND.message)
            return
        }

        val scopesArray = ScopesMapper.toStringArray(scopes)

        oneginiSDK.oneginiClient.userClient.registerUser(
            identityProvider, scopesArray,
            object : OneginiRegistrationHandler {
                override fun onSuccess(userProfile: UserProfile, customInfo: CustomInfo?) {
                    promise.resolve(UserProfileMapper.toWritableMap(userProfile))
                }

                override fun onError(error: OneginiRegistrationError) {
                    promise.reject(error.errorType.toString(), error.message)
                }
            }
        )
    }

    private fun getIdentityProvider(id: String?): OneginiIdentityProvider? {
        return oneginiSDK.oneginiClient.userClient.identityProviders.find { it.id == id }
    }
}
