package com.onegini.mobile.sdk.reactnative.handlers.registration

import android.net.Uri
import com.facebook.react.bridge.Arguments
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.onegini.mobile.sdk.reactnative.Constants
import com.onegini.mobile.sdk.reactnative.OneginiComponents

class RegistrationEventEmitter {
  fun onSendUrl(uri: Uri) {
    val dataMap = Arguments.createMap()
    dataMap.putString("url", uri.toString())
    OneginiComponents.reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(Constants.REGISTRATION_NOTIFICATION, dataMap)
  }
}
