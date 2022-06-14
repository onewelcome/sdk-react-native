package com.onegini.mobile.sdk.reactnative.handlers.mobileauthotp

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.onegini.mobile.sdk.android.model.entity.OneginiMobileAuthenticationRequest
import com.onegini.mobile.sdk.reactnative.Constants
import com.onegini.mobile.sdk.reactnative.mapers.OneginiMobileAuthenticationRequestMapper

interface IMobileAuthOtpRequestObserver {
    fun startAuthentication(request: OneginiMobileAuthenticationRequest?)

    fun finishAuthentication()
}

//

class MobileAuthOtpRequestObserver(private val reactApplicationContext: ReactApplicationContext) : IMobileAuthOtpRequestObserver {
    override fun startAuthentication(request: OneginiMobileAuthenticationRequest?) {
        val map = Arguments.createMap()
        map.putString("action", Constants.MOBILE_AUTH_OTP_START_AUTHENTICATION)
        OneginiMobileAuthenticationRequestMapper.add(map, request)
        reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java).emit(Constants.MOBILE_AUTH_OTP_NOTIFICATION, map)
    }

    override fun finishAuthentication() {
        val map = Arguments.createMap()
        map.putString("action", Constants.MOBILE_AUTH_OTP_FINISH_AUTHENTICATION)
        reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java).emit(Constants.MOBILE_AUTH_OTP_NOTIFICATION, map)
    }
}
