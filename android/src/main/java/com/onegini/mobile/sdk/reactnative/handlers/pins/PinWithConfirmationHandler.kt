package com.onegini.mobile.sdk.reactnative.handlers.pins

import android.content.Context
import com.onegini.mobile.sdk.android.handlers.OneginiPinValidationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiPinValidationError
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiPinCallback
import com.onegini.mobile.sdk.reactnative.Constants
import com.onegini.mobile.sdk.reactnative.Constants.PinFlow
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors
import java.util.*

class PinWithConfirmationHandler(
        private val originalHandler: OneginiPinCallback,
        private val oneginiSDK: OneginiSDK,
        private val context: Context
) {

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
        oneginiSDK.oneginiClient.userClient.validatePinWithPolicy(
            pin,
            object : OneginiPinValidationHandler {
                override fun onSuccess() {
                    this@PinWithConfirmationHandler.pin = pin
                    notifyOnSimpleAction(Constants.PIN_NOTIFICATION_CONFIRM_VIEW)
                }

                override fun onError(oneginiPinValidationError: OneginiPinValidationError) {
                    handlePinValidationError(oneginiPinValidationError)
                }
            }
        )
    }

    // TODO the second pin should be implemented in the app, not the plugin
    // https://onewelcome.atlassian.net/browse/RNP-75
    fun secondPinProvided(pin: CharArray?) {
        val pinsEqual = Arrays.equals(this.pin, pin)
        nullifyPinArray()
        if (pinsEqual) {
            originalHandler.acceptAuthenticationRequest(pin)
        } else {
            notifyOnError(OneginiWrapperErrors.PIN_ERROR_NOT_EQUAL)
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
        pinNotificationObserver?.onNotify(notifyAction, lastFlow, null, null)
    }

    fun notifyOnOpen(profileId: String? = null, data: Any? = null) {
        pinNotificationObserver?.onNotify(Constants.PIN_NOTIFICATION_OPEN_VIEW, lastFlow, profileId, data)
    }

    fun notifyOnError(error: OneginiWrapperErrors) {
        pinNotificationObserver?.onError(error.code.toInt(), error.message, lastFlow)
    }

    fun handlePinValidationError(error: OneginiPinValidationError) {
        pinNotificationObserver?.onError(error.getErrorType(), error.message ?: "", lastFlow)
    }
}
