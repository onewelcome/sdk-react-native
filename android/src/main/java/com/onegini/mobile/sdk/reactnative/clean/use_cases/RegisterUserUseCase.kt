package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableArray
import com.onegini.mobile.sdk.android.handlers.OneginiRegistrationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiRegistrationError
import com.onegini.mobile.sdk.android.model.OneginiIdentityProvider
import com.onegini.mobile.sdk.android.model.entity.CustomInfo
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError.IDENTITY_PROVIDER_NOT_FOUND
import com.onegini.mobile.sdk.reactnative.exception.rejectWrapperError
import com.onegini.mobile.sdk.reactnative.mapers.CustomInfoMapper
import com.onegini.mobile.sdk.reactnative.mapers.ScopesMapper
import com.onegini.mobile.sdk.reactnative.mapers.UserProfileMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegisterUserUseCase @Inject constructor(private val oneginiSDK: OneginiSDK) {

    operator fun invoke(identityProviderId: String?, scopes: ReadableArray?, promise: Promise) {
        val identityProvider = getIdentityProvider(identityProviderId)

        if (identityProvider == null && identityProviderId != null) {
            promise.rejectWrapperError(IDENTITY_PROVIDER_NOT_FOUND)
            return
        }

        val scopesArray = ScopesMapper.toStringArray(scopes)

        oneginiSDK.oneginiClient.userClient.registerUser(
            identityProvider, scopesArray,
            object : OneginiRegistrationHandler {
                override fun onSuccess(userProfile: UserProfile, customInfo: CustomInfo?) {
                    val result = Arguments.createMap()
                    UserProfileMapper.add(result, userProfile)
                    CustomInfoMapper.add(result, customInfo)
                    promise.resolve(result)
                }

                override fun onError(error: OneginiRegistrationError) {
                    val cause = error.cause?.message ?: error.message
                    promise.reject(error.errorType.toString(), cause)
                }
            }
        )
    }

    private fun getIdentityProvider(id: String?): OneginiIdentityProvider? {
        return oneginiSDK.oneginiClient.userClient.identityProviders.find { it.id == id }
    }
}
