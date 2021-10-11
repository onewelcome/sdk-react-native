package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.OneginiSDK

class GetAccessTokenUseCase(val oneginiSDK: OneginiSDK) {

    operator fun invoke(promise: Promise) {
        promise.resolve(oneginiSDK.oneginiClient.accessToken)
    }
}
