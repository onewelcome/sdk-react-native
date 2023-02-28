package com.onegini.mobile.sdk.reactnative.handlers.pins

import com.facebook.react.bridge.Arguments
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.onegini.mobile.sdk.reactnative.Constants
import com.onegini.mobile.sdk.reactnative.Constants.PinFlow
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PinAuthenticationEventEmitter @Inject constructor(private val deviceEventEmitter: DeviceEventManagerModule.RCTDeviceEventEmitter) {

  fun onPinOpen(profileId: String) {
    Arguments.createMap().apply {
      putString("action", Constants.PIN_NOTIFICATION_OPEN_VIEW)
      putString("flow", PinFlow.Authentication.toString())
      putString("profileId", profileId);
    }.also { dataMap ->
      deviceEventEmitter.emit(Constants.ONEWELCOME_PIN_AUTHENTICATION_NOTIFICATION, dataMap)
    }
  }

  fun onPinClose() {
    Arguments.createMap().apply {
      putString("action", Constants.PIN_NOTIFICATION_CLOSE_VIEW)
      putString("flow", PinFlow.Authentication.toString())
    }.also { dataMap ->
    deviceEventEmitter.emit(Constants.ONEWELCOME_PIN_AUTHENTICATION_NOTIFICATION, dataMap)
    }
  }

  fun onIncorrectPin(remainingAttempts: Int) {
    Arguments.createMap().apply {
      putString("action", Constants.PIN_NOTIFICATION_INCORRECT_PIN)
      putString("flow", PinFlow.Authentication.toString())
      putString("remainingFailureCount", remainingAttempts.toString())
      putInt("errorCode", OneginiWrapperError.WRONG_PIN_ERROR.code)
      putString("errorMsg", OneginiWrapperError.WRONG_PIN_ERROR.message)
    }.also { dataMap ->
      deviceEventEmitter.emit(Constants.ONEWELCOME_PIN_AUTHENTICATION_NOTIFICATION, dataMap)
    }
  }
}
