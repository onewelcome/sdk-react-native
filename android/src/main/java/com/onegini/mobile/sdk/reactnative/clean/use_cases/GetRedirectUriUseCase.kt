package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.OneginiSDK

class GetRedirectUriUseCase(private val oneginiSDK: OneginiSDK) {

    operator fun invoke(promise: Promise) {
        val uri = oneginiSDK.oneginiClient.configModel.redirectUri
        val result = Arguments.createMap()
        result.putString("redirectUri", uri)
        promise.resolve(result)
    }
}