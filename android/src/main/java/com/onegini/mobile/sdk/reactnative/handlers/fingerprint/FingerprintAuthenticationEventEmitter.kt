package com.onegini.mobile.sdk.reactnative.handlers.fingerprint

import com.facebook.react.bridge.Arguments
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.Constants
import com.onegini.mobile.sdk.reactnative.OneginiComponents.reactApplicationContext
import com.onegini.mobile.sdk.reactnative.mapers.UserProfileMapper

class FingerprintAuthenticationEventEmitter {
    fun startAuthentication(user: UserProfile) {
        val map = Arguments.createMap()
        UserProfileMapper.add(map, user)
        map.putString("action", Constants.FINGERPRINT_NOTIFICATION_START_AUTHENTICATION)
        reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java).emit(Constants.ONEWELCOME_FINGERPRINT_NOTIFICATION, map)
    }

    fun onNextAuthenticationAttempt() {
        val map = Arguments.createMap()
        map.putString("action", Constants.FINGERPRINT_NOTIFICATION_ON_NEXT_AUTHENTICATION_ATTEMPT)
        reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java).emit(Constants.ONEWELCOME_FINGERPRINT_NOTIFICATION, map)
    }

    fun onFingerprintCaptured() {
        val map = Arguments.createMap()
        map.putString("action", Constants.FINGERPRINT_NOTIFICATION_ON_FINGERPRINT_CAPTURED)
        reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java).emit(Constants.ONEWELCOME_FINGERPRINT_NOTIFICATION, map)
    }

    fun finishAuthentication() {
        val map = Arguments.createMap()
        map.putString("action", Constants.FINGERPRINT_NOTIFICATION_FINISH_AUTHENTICATION)
        reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java).emit(Constants.ONEWELCOME_FINGERPRINT_NOTIFICATION, map)
    }
}
