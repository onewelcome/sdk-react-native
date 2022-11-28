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
        val map = Arguments.createMap()
        map.putString("action", Constants.MOBILE_AUTH_OTP_START_AUTHENTICATION)
        OneginiMobileAuthenticationRequestMapper.add(map, request)
        deviceEventEmitter.emit(Constants.MOBILE_AUTH_OTP_NOTIFICATION, map)
    }

    fun finishAuthentication() {
        val map = Arguments.createMap()
        map.putString("action", Constants.MOBILE_AUTH_OTP_FINISH_AUTHENTICATION)
        deviceEventEmitter.emit(Constants.MOBILE_AUTH_OTP_NOTIFICATION, map)
    }
}
