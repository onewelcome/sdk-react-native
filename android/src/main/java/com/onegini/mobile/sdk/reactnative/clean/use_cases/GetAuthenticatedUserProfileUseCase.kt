package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.reactnative.mapers.UserProfileMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAuthenticatedUserProfileUseCase @Inject constructor(private val oneginiSDK: OneginiSDK) {

    operator fun invoke(promise: Promise) {
        oneginiSDK.oneginiClient.userClient.authenticatedUserProfile?.let { userProfile ->
            promise.resolve(UserProfileMapper.toWritableMap(userProfile))
        } ?: promise.reject(OneginiWrapperErrors.NO_PROFILE_AUTHENTICATED.code.toString(), OneginiWrapperErrors.NO_PROFILE_AUTHENTICATED.message)
    }
}
