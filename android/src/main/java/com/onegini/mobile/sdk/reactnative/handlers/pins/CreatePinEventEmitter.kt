package com.onegini.mobile.sdk.reactnative.handlers.pins

import com.facebook.react.bridge.Arguments
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.onegini.mobile.sdk.reactnative.Constants
import com.onegini.mobile.sdk.reactnative.Constants.PinFlow
import com.onegini.mobile.sdk.reactnative.OneginiComponents.reactApplicationContext

class CreatePinEventEmitter {

  fun onPinOpen(profileId: String, pinLength: Int) {
    val dataMap = Arguments.createMap()
    dataMap.putString("action", Constants.PIN_NOTIFICATION_OPEN_VIEW)
    dataMap.putString("flow", PinFlow.Create.toString())
    dataMap.putInt("data", pinLength)
    dataMap.putString("profileId", profileId);
    reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(Constants.ONEWELCOME_PIN_NOTIFICATION, dataMap)
  }

  fun onPinClose() {
    val dataMap = Arguments.createMap()
    dataMap.putString("action", Constants.PIN_NOTIFICATION_CLOSE_VIEW)
    dataMap.putString("flow", PinFlow.Create.toString())
    reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(Constants.ONEWELCOME_PIN_NOTIFICATION, dataMap)
  }

  fun onError(errorCode: Int, errorMessage: String) {
    val data = Arguments.createMap()
    data.putString("action", Constants.PIN_NOTIFICATION_SHOW_ERROR)
    data.putString("flow", PinFlow.Create.toString())
    data.putInt("errorType", errorCode)
    data.putString("errorMsg", errorMessage)
    reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(Constants.ONEWELCOME_PIN_NOTIFICATION, data)
  }
}