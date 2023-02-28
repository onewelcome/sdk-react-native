package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.handlers.pins.CreatePinRequestHandler
import javax.inject.Inject
import javax.inject.Singleton
import com.onegini.mobile.sdk.reactnative.exception.rejectRNException

@Singleton
class CancelPinCreationUseCase @Inject constructor(private val createPinRequestHandler: CreatePinRequestHandler) {
    operator fun invoke(promise: Promise) {
        try {
            createPinRequestHandler.cancelPin()
            promise.resolve(null)
        } catch (exception: OneginiReactNativeException) {
            promise.rejectRNException(exception)
        }
    }
}
