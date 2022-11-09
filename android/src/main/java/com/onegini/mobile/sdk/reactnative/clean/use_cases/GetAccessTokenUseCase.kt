package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors

class GetAccessTokenUseCase(val oneginiSDK: OneginiSDK) {

    operator fun invoke(promise: Promise) {
        oneginiSDK.oneginiClient.accessToken?.let { token ->
            promise.resolve(token)
            return
        }
        promise.reject(OneginiWrapperErrors.NO_PROFILE_AUTHENTICATED.code.toString(), OneginiWrapperErrors.NO_PROFILE_AUTHENTICATED.message)
    }
}
