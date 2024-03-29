package com.onegini.mobile.sdk.reactnative.handlers.registration

import android.net.Uri
import com.facebook.react.bridge.Arguments
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.onegini.mobile.sdk.reactnative.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegistrationEventEmitter @Inject constructor(private val deviceEventEmitter: DeviceEventManagerModule.RCTDeviceEventEmitter) {
  fun onSendUrl(uri: Uri) {
    Arguments.createMap().apply {
      putString("url", uri.toString())
    }.also { dataMap ->
      deviceEventEmitter.emit(Constants.REGISTRATION_NOTIFICATION, dataMap)
    }
  }
}
