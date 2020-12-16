package com.onegini.mobile.view.handlers.pins

import android.content.Context
import com.onegini.mobile.Constants
import com.onegini.mobile.Constants.PinFlow
import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.R
import com.onegini.mobile.sdk.android.handlers.OneginiPinValidationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiPinValidationError
import com.onegini.mobile.sdk.android.handlers.error.OneginiPinValidationError.PinValidationErrorType
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiPinCallback
import com.onegini.mobile.util.DeregistrationUtil
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
            notifyOnError(context.getString(R.string.pin_error_not_equal))
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

    fun notifyOnError(errorMessage: String?) {
        pinNotificationObserver?.onError(errorMessage ?: "", lastFlow)
    }

    fun handlePinValidationError(oneginiPinValidationError: OneginiPinValidationError) {
        @PinValidationErrorType val errorType = oneginiPinValidationError.errorType
        when (errorType) {
            OneginiPinValidationError.WRONG_PIN_LENGTH -> notifyOnError(context!!.getString(R.string.pin_error_invalid_length))
            OneginiPinValidationError.PIN_BLACKLISTED -> notifyOnError(context!!.getString(R.string.pin_error_blacklisted))
            OneginiPinValidationError.PIN_IS_A_SEQUENCE -> notifyOnError(context!!.getString(R.string.pin_error_sequence))
            OneginiPinValidationError.PIN_USES_SIMILAR_DIGITS -> notifyOnError(context!!.getString(R.string.pin_error_similar))
            OneginiPinValidationError.DEVICE_DEREGISTERED -> DeregistrationUtil(context).onDeviceDeregistered()
            OneginiPinValidationError.GENERAL_ERROR -> notifyOnError(oneginiPinValidationError.message)
            else -> notifyOnError(oneginiPinValidationError.message)
        }
    }
}