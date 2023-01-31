package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRedirectUriUseCase @Inject constructor(private val oneginiSDK: OneginiSDK) {

    operator fun invoke(promise: Promise) {
        promise.resolve(oneginiSDK.oneginiClient.configModel.redirectUri)
    }
}
