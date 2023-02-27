package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError
import com.onegini.mobile.sdk.android.handlers.OneginiDeregisterUserProfileHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiDeregistrationError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeregisterUserUseCase @Inject constructor(
    private val oneginiSDK: OneginiSDK,
    private val getUserProfileUseCase: GetUserProfileUseCase
) {
    operator fun invoke(profileId: String, promise: Promise) {
        val userProfile = getUserProfileUseCase(profileId)

        if (userProfile == null) {
            promise.reject(OneginiWrapperError.PROFILE_DOES_NOT_EXIST.code.toString(), OneginiWrapperError.PROFILE_DOES_NOT_EXIST.message)
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
