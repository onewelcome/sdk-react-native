package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.rejectRNException
import com.onegini.mobile.sdk.reactnative.handlers.pins.PinAuthenticationRequestHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CancelPinAuthenticationUseCase @Inject constructor(private val pinAuthenticationRequestHandler: PinAuthenticationRequestHandler) {
    operator fun invoke(promise: Promise) {
        try {
            pinAuthenticationRequestHandler.denyAuthenticationRequest()
            promise.resolve(null)
        } catch (exception: OneginiReactNativeException) {
            promise.rejectRNException(exception)
        }
    }
}
