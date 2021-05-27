package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.exception.OneginiWrapperErrors
import com.onegini.mobile.mapers.CustomInfoMapper
import com.onegini.mobile.mapers.UserProfileMapper
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiAuthenticationError
import com.onegini.mobile.sdk.android.model.entity.CustomInfo
import com.onegini.mobile.sdk.android.model.entity.UserProfile

class AuthenticateUserUseCase(private val oneginiSDK: OneginiSDK) {

    operator fun invoke(profileId: String?, promise: Promise) {
        val userProfile = try {
            UserProfile(profileId)
        } catch (e: IllegalArgumentException) {
            null
        }

        if (userProfile == null) {
            promise.reject(OneginiWrapperErrors.USER_PROFILE_IS_NULL.code, OneginiWrapperErrors.USER_PROFILE_IS_NULL.message)
            return
        }

        oneginiSDK.oneginiClient.userClient.authenticateUser(
            userProfile,
            object : OneginiAuthenticationHandler {
                override fun onSuccess(userProfile: UserProfile?, customInfo: CustomInfo?) {
                    val result = Arguments.createMap()
                    UserProfileMapper.add(result, userProfile)
                    CustomInfoMapper.add(result, customInfo)
                    promise.resolve(result)
                }

                override fun onError(error: OneginiAuthenticationError) {
                    promise.reject(error.errorType.toString(), error.message)
                }
            }
        )
    }
}
