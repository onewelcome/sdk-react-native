package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.android.handlers.OneginiDeregisterUserProfileHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiDeregistrationError
import com.onegini.mobile.sdk.android.model.entity.UserProfile

class DeregisterUserUseCase(private val oneginiSDK: OneginiSDK) {

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

        oneginiSDK.oneginiClient.userClient.deregisterUser(
            userProfile,
            object : OneginiDeregisterUserProfileHandler {
                override fun onSuccess() {
                    promise.resolve(null)
                }

                override fun onError(error: OneginiDeregistrationError?) {
                    promise.reject(error?.errorType.toString(), error?.message)
                }
            }
        )
    }
}
