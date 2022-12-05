package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.Constants
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.AUTHENTICATION_NOT_IN_PROGRESS
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS
import com.onegini.mobile.sdk.reactnative.handlers.pins.CreatePinRequestHandler
import com.onegini.mobile.sdk.reactnative.handlers.pins.PinAuthenticationRequestHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubmitPinUseCase @Inject constructor(
    private val createPinRequestHandler: CreatePinRequestHandler,
    private val pinAuthenticationRequestHandler: PinAuthenticationRequestHandler
) {
    operator fun invoke(pinFlow: String?, pin: String, promise: Promise) {
        when (pinFlow) {
            Constants.PinFlow.Authentication.toString() -> {
                return handleSubmitAuthPin(pin, promise)
            }
            Constants.PinFlow.Create.toString() -> {
                return handleSubmitCreatePin(pin, promise)
            }
        }
    }

    private fun handleSubmitCreatePin(pin: String, promise: Promise) {
        return try {
            createPinRequestHandler.onPinProvided(pin.toCharArray())
            promise.resolve(null)
        } catch (exception: OneginiReactNativeException) {
            promise.reject(
                REGISTRATION_NOT_IN_PROGRESS.code.toString(),
                REGISTRATION_NOT_IN_PROGRESS.message
            )
        }
    }

    private fun handleSubmitAuthPin(pin: String, promise: Promise) {
        return try {
            pinAuthenticationRequestHandler.acceptAuthenticationRequest(pin.toCharArray())
            promise.resolve(null)
        } catch (exception: OneginiReactNativeException) {
            promise.reject(
                AUTHENTICATION_NOT_IN_PROGRESS.code.toString(),
                AUTHENTICATION_NOT_IN_PROGRESS.message
            )
        }
    }
}
