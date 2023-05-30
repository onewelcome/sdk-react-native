package com.onegini.mobile.sdk.reactnative.handlers.pins

import com.facebook.react.bridge.Arguments
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.onegini.mobile.sdk.reactnative.Constants
import com.onegini.mobile.sdk.reactnative.Constants.PinFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreatePinEventEmitter @Inject constructor(private val deviceEventEmitter: DeviceEventManagerModule.RCTDeviceEventEmitter) {
  //As an improvement you can move strings into private const val in companion object of this class
  fun onPinOpen(profileId: String, pinLength: Int) {
    Arguments.createMap().apply {
      putString("action", Constants.PIN_NOTIFICATION_OPEN_VIEW)
      putString("flow", PinFlow.Create.toString())
      putInt("pinLength", pinLength)
      putString("profileId", profileId);
    }.also { dataMap ->
      deviceEventEmitter.emit(Constants.ONEWELCOME_PIN_CREATE_NOTIFICATION, dataMap)
    }
  }

  fun onPinClose() {
    Arguments.createMap().apply {
      putString("action", Constants.PIN_NOTIFICATION_CLOSE_VIEW)
      putString("flow", PinFlow.Create.toString())
    }.also { dataMap ->
      deviceEventEmitter.emit(Constants.ONEWELCOME_PIN_CREATE_NOTIFICATION, dataMap)
    }
  }

  fun onPinNotAllowed(errorCode: Int, errorMessage: String) {
    Arguments.createMap().apply {
      putString("action", Constants.PIN_NOTIFICATION_PIN_NOT_ALLOWED)
      putString("flow", PinFlow.Create.toString())
      putInt("errorCode", errorCode)
      putString("errorMsg", errorMessage)
    }.also { dataMap ->
      deviceEventEmitter.emit(Constants.ONEWELCOME_PIN_CREATE_NOTIFICATION, dataMap)
    }
  }
}
