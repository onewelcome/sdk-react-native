package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.android.handlers.OneginiDeregisterUserProfileHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiDeregistrationError

class DeregisterUserUseCase(
    private val oneginiSDK: OneginiSDK,
    private val getUserProfileUseCase: GetUserProfileUseCase = GetUserProfileUseCase(oneginiSDK)
) {
    operator fun invoke(profileId: String, promise: Promise) {
        val userProfile = getUserProfileUseCase(profileId)

        if (userProfile == null) {
            promise.reject(OneginiWrapperErrors.PROFILE_DOES_NOT_EXIST.code, OneginiWrapperErrors.PROFILE_DOES_NOT_EXIST.message)
            return
        }

        oneginiSDK.oneginiClient.userClient.deregisterUser(
            userProfile,
            object : OneginiDeregisterUserProfileHandler {
                override fun onSuccess() {
                    promise.resolve(null)
                }

                override fun onError(error: OneginiDeregistrationError) {
                    promise.reject(error?.errorType.toString(), error?.message)
                }
            }
        )
    }
}
