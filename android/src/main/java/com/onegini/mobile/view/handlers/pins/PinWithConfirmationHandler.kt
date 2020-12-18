package com.onegini.mobile.view.handlers.pins

import android.content.Context
import com.onegini.mobile.Constants
import com.onegini.mobile.Constants.PinFlow
import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.exception.EmptyOneginiErrorDetails
import com.onegini.mobile.exception.OneginReactNativeException
import com.onegini.mobile.sdk.android.handlers.OneginiPinValidationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiError
import com.onegini.mobile.sdk.android.handlers.error.OneginiPinValidationError
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiPinCallback
import java.util.*

class PinWithConfirmationHandler(private val originalHandler: OneginiPinCallback,
                                 private val oneginiSDK: OneginiSDK,
                                 private val context: Context) {

    var pinNotificationObserver: PinNotificationObserver? = null

    private var pin: CharArray? = null

    var lastFlow: PinFlow = PinFlow.Create

    fun onPinProvided(pin: CharArray?, flow: PinFlow) {
        lastFlow = flow
        if (isPinSet) {
            secondPinProvided(pin)
        } else {
            firstPinProvided(pin)
        }
    }

    private fun firstPinProvided(pin: CharArray?) {
        oneginiSDK.oneginiClient.userClient.validatePinWithPolicy(pin, object : OneginiPinValidationHandler {
            override fun onSuccess() {
                this@PinWithConfirmationHandler.pin = pin
                notifyOnSimpleAction(Constants.PIN_NOTIFICATION_CONFIRM_VIEW)
            }

            override fun onError(oneginiPinValidationError: OneginiPinValidationError) {
                handlePinValidationError(oneginiPinValidationError)
            }
        })
    }

    fun secondPinProvided(pin: CharArray?) {
        val pinsEqual = Arrays.equals(this.pin, pin)
        nullifyPinArray()
        if (pinsEqual) {
            originalHandler.acceptAuthenticationRequest(pin)
        } else {
            notifyOnError(OneginReactNativeException(
                    OneginReactNativeException.PIN_ERROR_NOT_EQUAL,
                    EmptyOneginiErrorDetails(),
                    "PIN was not the same, choose PIN",
                    null
            ))
        }
    }

    fun pinCancelled(flow: PinFlow) {
        lastFlow = flow
        nullifyPinArray()
        originalHandler.denyAuthenticationRequest()
    }

    private val isPinSet: Boolean
        private get() = pin != null

    private fun nullifyPinArray() {
        if (isPinSet) {
            val arraySize = pin!!.size
            for (i in 0 until arraySize) {
                pin!![i] = '\u0000'
            }
            pin = null
        }
    }

    fun notifyOnSimpleAction(notifyAction: String) {
        pinNotificationObserver?.onNotify(notifyAction, lastFlow)
    }

    fun notifyOnOpen() {
        pinNotificationObserver?.onNotify(Constants.PIN_NOTIFICATION_OPEN_VIEW, lastFlow)
    }

    fun notifyOnError(error: OneginiError?) {
        pinNotificationObserver?.onError(error, lastFlow)
    }

    fun handlePinValidationError(error: OneginiPinValidationError) {
        notifyOnError(error)
    }
}
