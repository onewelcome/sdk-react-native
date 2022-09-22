package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.reactnative.mapers.CustomInfoMapper
import com.onegini.mobile.sdk.reactnative.mapers.UserProfileMapper
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiAuthenticationError
import com.onegini.mobile.sdk.android.model.entity.CustomInfo
import com.onegini.mobile.sdk.android.model.entity.UserProfile

class AuthenticateUserUseCase(
    private val oneginiSDK: OneginiSDK,
    private val getRegisteredAuthenticatorsUseCase: GetRegisteredAuthenticatorsUseCase = GetRegisteredAuthenticatorsUseCase(oneginiSDK),
    private val getUserProfileUseCase: GetUserProfileUseCase = GetUserProfileUseCase(oneginiSDK)
) {
    operator fun invoke(profileId: String, authenticatorId: String, promise: Promise) {
        val userProfile = getUserProfileUseCase(profileId)

        if (userProfile == null) {
            promise.reject(OneginiWrapperErrors.USER_PROFILE_IS_NULL.code, OneginiWrapperErrors.USER_PROFILE_IS_NULL.message)
            return
        }

        val allAuthenticators = getRegisteredAuthenticatorsUseCase.getList(userProfile)
        val authenticator = allAuthenticators.find { it.id == authenticatorId }

        val handler = object : OneginiAuthenticationHandler {
            override fun onSuccess(userProfile: UserProfile, customInfo: CustomInfo?) {
                val result = Arguments.createMap()
                UserProfileMapper.add(result, userProfile)
                CustomInfoMapper.add(result, customInfo)
                promise.resolve(result)
            }

            override fun onError(error: OneginiAuthenticationError) {
                promise.reject(error.errorType.toString(), error.message)
            }
        }

        if (authenticator != null) {
            oneginiSDK.oneginiClient.userClient.authenticateUser(
                userProfile,
                authenticator,
                handler
            )
        } else {
            oneginiSDK.oneginiClient.userClient.authenticateUser(
                userProfile,
                handler
            )
        }
    }
}
