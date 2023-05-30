package com.onegini.mobile.sdk.reactnative.handlers.mobileauthotp

import com.facebook.react.bridge.Arguments
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.onegini.mobile.sdk.android.model.entity.OneginiMobileAuthenticationRequest
import com.onegini.mobile.sdk.reactnative.Constants
import com.onegini.mobile.sdk.reactnative.mapers.OneginiMobileAuthenticationRequestMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MobileAuthOtpRequestEventEmitter @Inject constructor(private val deviceEventEmitter: DeviceEventManagerModule.RCTDeviceEventEmitter) {
  fun startAuthentication(request: OneginiMobileAuthenticationRequest) {
    Arguments.createMap().apply {
      //As an improvement you can move strings into private const val in companion object of this class
      putString("action", Constants.MOBILE_AUTH_OTP_START_AUTHENTICATION)
      OneginiMobileAuthenticationRequestMapper.add(this, request)
    }.also { dataMap ->
      deviceEventEmitter.emit(Constants.MOBILE_AUTH_OTP_NOTIFICATION, dataMap)
    }
  }

  fun finishAuthentication() {
    Arguments.createMap().apply {
      putString("action", Constants.MOBILE_AUTH_OTP_FINISH_AUTHENTICATION)
    }.also { dataMap ->
      deviceEventEmitter.emit(Constants.MOBILE_AUTH_OTP_NOTIFICATION, dataMap)
    }
  }
}
