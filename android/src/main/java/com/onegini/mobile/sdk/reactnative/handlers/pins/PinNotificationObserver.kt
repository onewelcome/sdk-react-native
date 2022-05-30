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
    fun onNotify(event: String, flow: PinFlow, profileId: String?, data: Any?)
    fun onError(error: OneginiError?, flow: PinFlow)
    fun onWrongPin(error: OneginiError, remainingAttempts: Int)
}

//

class PinNotificationObserver(private val reactApplicationContext: ReactApplicationContext) : IPinNotificationObserver {

    override fun onNotify(event: String, flow: PinFlow, profileId: String?, data: Any?) {
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

                if(profileId != null) {
                    dataMap.putString("profileId", profileId);
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
            reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java).emit(Constants.ONEWELCOME_PIN_NOTIFICATION, dataMap)
        }
    }

    override fun onError(error: OneginiError?, flow: PinFlow) {
        val data = Arguments.createMap()
        data.putString("action", Constants.PIN_NOTIFICATION_SHOW_ERROR)
        data.putString("flow", flow.flowString)
        OneginiErrorMapper.update(data, error)
        reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java).emit(Constants.ONEWELCOME_PIN_NOTIFICATION, data)
    }

    // This isn't the most logical way to send the remaining attempts to the plugin,
    // but I did it to not have to modify iOS/JS parts as well
    override fun onWrongPin(error: OneginiError, remainingAttempts: Int) {
        val userInfo = Arguments.createMap()
        userInfo.putString("remainingFailureCount", remainingAttempts.toString())

        val dataMap = Arguments.createMap()
        dataMap.putString("action", Constants.PIN_NOTIFICATION_SHOW_ERROR)
        dataMap.putString("flow", Constants.PinFlow.Authentication.flowString)
        dataMap.putMap("userInfo", userInfo)
        OneginiErrorMapper.update(dataMap, error)
        reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                .emit(Constants.ONEWELCOME_PIN_NOTIFICATION, dataMap)
    }
}
