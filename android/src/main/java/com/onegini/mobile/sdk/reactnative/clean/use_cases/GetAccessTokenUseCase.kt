package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAccessTokenUseCase @Inject constructor(val oneginiSDK: OneginiSDK) {

    operator fun invoke(promise: Promise) {
        oneginiSDK.oneginiClient.accessToken?.let { token ->
            promise.resolve(token)
            return
        }
        promise.reject(OneginiWrapperErrors.NO_PROFILE_AUTHENTICATED.code, OneginiWrapperErrors.NO_PROFILE_AUTHENTICATED.message)
    }
}
