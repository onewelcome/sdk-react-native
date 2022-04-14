package com.onegini.mobile.sdk.reactnative.handlers.pins

import android.util.Log
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.onegini.mobile.sdk.reactnative.Constants
import com.onegini.mobile.sdk.reactnative.Constants.PinFlow
import com.onegini.mobile.sdk.reactnative.mapers.OneginiErrorMapper
import com.onegini.mobile.sdk.android.handlers.error.OneginiError

interface IPinNotificationObserver {
    fun onNotify(event: String, flow: PinFlow, data: Any?)
    fun onError(error: OneginiError?, flow: PinFlow)
}

//

class PinNotificationObserver(private val reactApplicationContext: ReactApplicationContext) : IPinNotificationObserver {

    override fun onNotify(event: String, flow: PinFlow, data: Any?) {
        val dataMap = Arguments.createMap()
        when (event) {
            Constants.PIN_NOTIFICATION_OPEN_VIEW -> {
                dataMap.putString("action", Constants.PIN_NOTIFICATION_OPEN_VIEW)
                dataMap.putString("flow", flow.flowString)

                if(data != null){
                    if(data is Int){
                        dataMap.putInt("data", data)
                    } else {
                        dataMap.putString("data", data.toString())
                    }
                }
            }
            Constants.PIN_NOTIFICATION_CONFIRM_VIEW -> {
                dataMap.putString("action", Constants.PIN_NOTIFICATION_CONFIRM_VIEW)
                dataMap.putString("flow", flow.flowString)
            }
            Constants.PIN_NOTIFICATION_CLOSE_VIEW -> {
                dataMap.putString("action", Constants.PIN_NOTIFICATION_CLOSE_VIEW)
                dataMap.putString("flow", flow.flowString)
            }
            Constants.PIN_NOTIFICATION_CHANGED -> {
                dataMap.putString("action", Constants.PIN_NOTIFICATION_CHANGED)
                dataMap.putString("flow", flow.flowString)
            }
            else -> Log.e("PinNotificationObserver", "Got unsupported PIN notification type: $event")
        }
        if (dataMap.getString("action") != null) {
            reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java).emit(Constants.ONEGINI_PIN_NOTIFICATION, dataMap)
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
