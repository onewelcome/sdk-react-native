//@todo Later will be transferred to RN Wrapper
package com.onegini.mobile.sdk.reactnative.handlers.pins

import android.content.Context
import com.onegini.mobile.sdk.android.handlers.OneginiPinValidationHandler
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.android.handlers.request.OneginiCreatePinRequestHandler
import com.onegini.mobile.sdk.reactnative.Constants.PinFlow
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiPinCallback
import com.onegini.mobile.sdk.android.handlers.error.OneginiPinValidationError
import com.onegini.mobile.sdk.reactnative.Constants
import com.onegini.mobile.sdk.reactnative.Constants.PIN_NOTIFICATION_CLOSE_VIEW
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors

class CreatePinRequestHandler(private val context: Context, private val oneginiSDK: OneginiSDK) :
    OneginiCreatePinRequestHandler {
    private var originalHandler: OneginiPinCallback? = null


    private var pinNotificationHandler: PinNotificationObserver? = null
    private var lastPinFlow: PinFlow = PinFlow.Create

    private var pin: CharArray? = null

    fun setPinNotificationObserver(pinNotificationHandler: PinNotificationObserver?) {
        this.pinNotificationHandler = pinNotificationHandler
    }

    override fun startPinCreation(
        userProfile: UserProfile,
        oneginiPinCallback: OneginiPinCallback,
        pinLength: Int
    ) {
        originalHandler = oneginiPinCallback
        notifyOnOpen(userProfile.profileId, pinLength)
    }

    override fun onNextPinCreationAttempt(oneginiPinValidationError: OneginiPinValidationError) {
            handlePinValidationError(oneginiPinValidationError)
    }

    override fun finishPinCreation() {
        notifyOnSimpleAction(PIN_NOTIFICATION_CLOSE_VIEW)
    }

    fun setPinFlow(flow: PinFlow) {
        lastPinFlow = flow
    }

    fun onPinProvided(pin: CharArray?, flow: PinFlow) {
        lastPinFlow = flow
        oneginiSDK.oneginiClient.userClient.validatePinWithPolicy(
            pin,
            object : OneginiPinValidationHandler {
                override fun onSuccess() {
                    this@CreatePinRequestHandler.pin = pin
                    originalHandler!!.acceptAuthenticationRequest(pin)
                }

                override fun onError(oneginiPinValidationError: OneginiPinValidationError) {
                    handlePinValidationError(oneginiPinValidationError)
                }
            }
        )
    }

    fun pinCancelled(flow: PinFlow) {
        lastPinFlow = flow
        nullifyPinArray()
        originalHandler!!.denyAuthenticationRequest()
    }

    private fun nullifyPinArray() {
        if (pin != null) {
            val arraySize = pin!!.size
            for (i in 0 until arraySize) {
                pin!![i] = '\u0000'
            }
            pin = null
        }
    }

    fun handlePinValidationError(error: OneginiPinValidationError) {
        pinNotificationHandler?.onError(error.getErrorType(), error.message ?: "", lastPinFlow)
    }

    fun notifyOnSimpleAction(notifyAction: String) {
        pinNotificationHandler?.onNotify(notifyAction, lastPinFlow, null, null)
    }

    fun notifyOnOpen(profileId: String? = null, data: Any? = null) {
        pinNotificationHandler?.onNotify(Constants.PIN_NOTIFICATION_OPEN_VIEW, lastPinFlow, profileId, data)
    }

    fun notifyOnError(error: OneginiWrapperErrors) {
        pinNotificationHandler?.onError(error.code.toInt(), error.message, lastPinFlow)
    }
}