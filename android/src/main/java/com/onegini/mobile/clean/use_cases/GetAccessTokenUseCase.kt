package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.OneginiComponets

class GetAccessTokenUseCase {

    operator fun invoke(promise: Promise) {
        promise.resolve(OneginiComponets.oneginiSDK.oneginiClient.accessToken)
    }
}
