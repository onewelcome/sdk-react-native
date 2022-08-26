package com.onegini.mobile.sdk.reactnative.handlers.pins

import com.facebook.react.bridge.Arguments
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.onegini.mobile.sdk.reactnative.Constants
import com.onegini.mobile.sdk.reactnative.Constants.PinFlow
import com.onegini.mobile.sdk.reactnative.OneginiComponents.reactApplicationContext
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors


class PinAuthenticationEventEmitter() {
  fun onPinOpen(profileId: String) {
    val dataMap = Arguments.createMap()
    dataMap.putString("action", Constants.PIN_NOTIFICATION_OPEN_VIEW)
    dataMap.putString("flow", PinFlow.Authentication.toString())
    dataMap.putString("profileId", profileId);
    reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(Constants.ONEWELCOME_PIN_NOTIFICATION, dataMap)
  }

  fun onPinClose() {
    val dataMap = Arguments.createMap()
    dataMap.putString("action", Constants.PIN_NOTIFICATION_CLOSE_VIEW)
    dataMap.putString("flow", PinFlow.Authentication.toString())
    reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(Constants.ONEWELCOME_PIN_NOTIFICATION, dataMap)
  }

  fun onError(errorCode: Int, errorMessage: String) {
    val data = Arguments.createMap()
    data.putString("action", Constants.PIN_NOTIFICATION_SHOW_ERROR)
    data.putString("flow", PinFlow.Authentication.toString())
    data.putInt("errorType", errorCode)
    data.putString("errorMsg", errorMessage)
    reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(Constants.ONEWELCOME_PIN_NOTIFICATION, data)
  }
  // TODO: This is a remnant from some hacky old code, let's refactor this :^)
  // This isn't the most logical way to send the remaining attempts to the plugin,
  // but I did it to not have to modify iOS/JS parts as well
  fun onWrongPin(remainingAttempts: Int) {
    val userInfo = Arguments.createMap()
    userInfo.putString("remainingFailureCount", remainingAttempts.toString())

    val dataMap = Arguments.createMap()
    dataMap.putString("action", Constants.PIN_NOTIFICATION_SHOW_ERROR)
    dataMap.putString("flow", Constants.PinFlow.Authentication.toString())
    dataMap.putMap("userInfo", userInfo)
    dataMap.putInt("errorType", OneginiWrapperErrors.WRONG_PIN_ERROR.code.toInt())
    dataMap.putString("errorMsg", OneginiWrapperErrors.WRONG_PIN_ERROR.message)
    reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(Constants.ONEWELCOME_PIN_NOTIFICATION, dataMap)
  }
}