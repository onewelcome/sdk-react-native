package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.OneginiComponets
import com.onegini.mobile.sdk.android.handlers.error.OneginiChangePinError
import com.onegini.mobile.view.handlers.pins.ChangePinHandler

class StartChangePinFlowUseCase {

    operator fun invoke(promise: Promise) {
        OneginiComponets.oneginiSDK.changePinHandler.onStartChangePin(object : ChangePinHandler.ChangePinHandlerResponse {
            override fun onSuccess() {
                promise.resolve(null)
            }

            override fun onError(error: OneginiChangePinError?) {
                promise.reject(error?.errorType.toString(), error?.message)
            }
        })
    }
}
