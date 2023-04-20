package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.Constants
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError.AUTHENTICATION_NOT_IN_PROGRESS
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError.INCORRECT_PIN_FLOW
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError.REGISTRATION_NOT_IN_PROGRESS
import com.onegini.mobile.sdk.reactnative.exception.rejectWrapperError
import com.onegini.mobile.sdk.reactnative.handlers.pins.CreatePinRequestHandler
import com.onegini.mobile.sdk.reactnative.handlers.pins.PinAuthenticationRequestHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubmitPinUseCase @Inject constructor(
    private val createPinRequestHandler: CreatePinRequestHandler,
    private val pinAuthenticationRequestHandler: PinAuthenticationRequestHandler
) {
    operator fun invoke(pinFlow: String, pin: String, promise: Promise) {
        return when (pinFlow) {
            Constants.PinFlow.Authentication.toString() -> handleSubmitAuthPin(pin, promise)
            Constants.PinFlow.Create.toString() -> handleSubmitCreatePin(pin, promise)
            else -> promise.rejectWrapperError(INCORRECT_PIN_FLOW)
        }
    }

    private fun handleSubmitCreatePin(pin: String, promise: Promise) {
        return try {
            createPinRequestHandler.onPinProvided(pin.toCharArray())
            promise.resolve(null)
        } catch (exception: OneginiReactNativeException) {
            promise.rejectWrapperError(REGISTRATION_NOT_IN_PROGRESS)
        }
    }

    private fun handleSubmitAuthPin(pin: String, promise: Promise) {
        return try {
            pinAuthenticationRequestHandler.acceptAuthenticationRequest(pin.toCharArray())
            promise.resolve(null)
        } catch (exception: OneginiReactNativeException) {
            promise.rejectWrapperError(AUTHENTICATION_NOT_IN_PROGRESS)
        }
    }
}
