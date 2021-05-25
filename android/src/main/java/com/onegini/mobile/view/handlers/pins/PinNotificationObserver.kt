package com.onegini.mobile.view.handlers.pins

import android.util.Log
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.onegini.mobile.Constants
import com.onegini.mobile.Constants.PinFlow
import com.onegini.mobile.mapers.OneginiErrorMapper
import com.onegini.mobile.sdk.android.handlers.error.OneginiError


interface IPinNotificationObserver {
    fun onNotify(event: String, flow: PinFlow)
    fun onError(error: OneginiError?, flow: PinFlow)
}

//

class PinNotificationObserver(private val reactApplicationContext: ReactApplicationContext): IPinNotificationObserver {

    override fun onNotify(event: String, flow: PinFlow) {
        val data = Arguments.createMap()
        when (event) {
            Constants.PIN_NOTIFICATION_OPEN_VIEW -> {
                data.putString("action", Constants.PIN_NOTIFICATION_OPEN_VIEW)
                data.putString("flow", flow.flowString)
            }
            Constants.PIN_NOTIFICATION_CONFIRM_VIEW -> {
                data.putString("action", Constants.PIN_NOTIFICATION_CONFIRM_VIEW)
                data.putString("flow", flow.flowString)
            }
            Constants.PIN_NOTIFICATION_CLOSE_VIEW -> {
                data.putString("action", Constants.PIN_NOTIFICATION_CLOSE_VIEW)
                data.putString("flow", flow.flowString)
            }
            Constants.PIN_NOTIFICATION_CHANGED -> {
                data.putString("action", Constants.PIN_NOTIFICATION_CHANGED)
                data.putString("flow", flow.flowString)
            }
            else -> Log.e("PinNotificationObserver", "Got unsupported PIN notification type: $event")
        }
        if (data.getString("action") != null) {
            reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java).emit(Constants.ONEGINI_PIN_NOTIFICATION, data)
        }
    }

    override fun onError(error: OneginiError?, flow: PinFlow) {
        val data = Arguments.createMap()
        data.putString("action", Constants.PIN_NOTIFICATION_SHOW_ERROR)
        data.putString("flow", flow.flowString)
        OneginiErrorMapper.update(data, error)
        reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java).emit(Constants.ONEGINI_PIN_NOTIFICATION, data)
    }

}
