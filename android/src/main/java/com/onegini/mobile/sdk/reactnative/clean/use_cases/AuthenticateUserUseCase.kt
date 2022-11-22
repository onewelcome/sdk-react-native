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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticateUserUseCase @Inject constructor(
    private val oneginiSDK: OneginiSDK,
    private val getRegisteredAuthenticatorsUseCase: GetRegisteredAuthenticatorsUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase
) {
    operator fun invoke(profileId: String, authenticatorId: String?, promise: Promise) {
        val userProfile = getUserProfileUseCase(profileId)

        if (userProfile == null) {
            promise.reject(OneginiWrapperErrors.PROFILE_DOES_NOT_EXIST.code, OneginiWrapperErrors.PROFILE_DOES_NOT_EXIST.message)
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
