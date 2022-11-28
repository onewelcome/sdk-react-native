package com.onegini.mobile.sdk.reactnative.handlers.pins

import com.facebook.react.bridge.Arguments
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.onegini.mobile.sdk.reactnative.Constants
import com.onegini.mobile.sdk.reactnative.Constants.PinFlow
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PinAuthenticationEventEmitter @Inject constructor(private val deviceEventEmitter: DeviceEventManagerModule.RCTDeviceEventEmitter) {

  fun onPinOpen(profileId: String) {
    val dataMap = Arguments.createMap()
    dataMap.putString("action", Constants.PIN_NOTIFICATION_OPEN_VIEW)
    dataMap.putString("flow", PinFlow.Authentication.toString())
    dataMap.putString("profileId", profileId);
    deviceEventEmitter.emit(Constants.ONEWELCOME_PIN_AUTHENTICATION_NOTIFICATION, dataMap)
  }

  fun onPinClose() {
    val dataMap = Arguments.createMap()
    dataMap.putString("action", Constants.PIN_NOTIFICATION_CLOSE_VIEW)
    dataMap.putString("flow", PinFlow.Authentication.toString())
    deviceEventEmitter.emit(Constants.ONEWELCOME_PIN_AUTHENTICATION_NOTIFICATION, dataMap)
  }

  fun onIncorrectPin(remainingAttempts: Int) {
    val dataMap = Arguments.createMap()
    dataMap.putString("action", Constants.PIN_NOTIFICATION_INCORRECT_PIN)
    dataMap.putString("flow", PinFlow.Authentication.toString())
    dataMap.putString("remainingFailureCount", remainingAttempts.toString())
    dataMap.putInt("errorType", OneginiWrapperErrors.WRONG_PIN_ERROR.code)
    dataMap.putString("errorMsg", OneginiWrapperErrors.WRONG_PIN_ERROR.message)
    deviceEventEmitter.emit(Constants.ONEWELCOME_PIN_AUTHENTICATION_NOTIFICATION, dataMap)
  }
}
