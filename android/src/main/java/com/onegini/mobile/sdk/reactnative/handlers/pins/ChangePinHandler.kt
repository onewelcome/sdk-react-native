package com.onegini.mobile.sdk.reactnative.handlers.pins

import com.onegini.mobile.sdk.android.handlers.OneginiChangePinHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiChangePinError
import com.onegini.mobile.sdk.reactnative.Constants.PIN_NOTIFICATION_CHANGED
import com.onegini.mobile.sdk.reactnative.Constants.PinFlow
import com.onegini.mobile.sdk.reactnative.OneginiSDK

class ChangePinHandler(private val oneginiSDK: OneginiSDK) : OneginiChangePinHandler {

    var pinNotificationObserver: PinNotificationObserver? = null

    private var response: ChangePinHandlerResponse? = null

    override fun onSuccess() {
        pinNotificationObserver?.onNotify(PIN_NOTIFICATION_CHANGED, PinFlow.Change, null, null)
        oneginiSDK.createPinRequestHandler.setPinFlow(PinFlow.Create)
        response?.onSuccess()
    }

    override fun onError(error: OneginiChangePinError) {
        // todo Fix error message
        pinNotificationObserver?.onError(error.getErrorType(), error.message ?: "", PinFlow.Change)
        oneginiSDK.createPinRequestHandler.setPinFlow(PinFlow.Create)
        response?.onError(error)
    }

    fun onStartChangePin(response: ChangePinHandlerResponse) {
        this.response = response
        oneginiSDK.createPinRequestHandler.setPinFlow(PinFlow.Change)
        oneginiSDK.oneginiClient.userClient.changePin(this)
    }

    fun onPinProvided(pin: CharArray?) {
        oneginiSDK.createPinRequestHandler.onPinProvided(pin, PinFlow.Change)
    }

    fun pinCancelled() {
        oneginiSDK.createPinRequestHandler.pinCancelled(PinFlow.Change)
    }

    interface ChangePinHandlerResponse {
        fun onSuccess()

        fun onError(error: OneginiChangePinError?)
    }
}
