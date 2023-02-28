package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.OneginiChangePinHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiChangePinError
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.rejectOneginiException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ChangePinUseCase @Inject constructor(private val oneginiSDK: OneginiSDK) {
    operator fun invoke(promise: Promise) {
        oneginiSDK.oneginiClient.userClient.changePin(object : OneginiChangePinHandler {
            override fun onSuccess() {
                promise.resolve(null)
            }

            override fun onError(error: OneginiChangePinError) {
                promise.rejectOneginiException(error)
            }
        })
    }
}
