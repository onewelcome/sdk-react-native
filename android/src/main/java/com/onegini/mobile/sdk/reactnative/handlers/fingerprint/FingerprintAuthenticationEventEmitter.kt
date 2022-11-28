package com.onegini.mobile.sdk.reactnative.handlers.fingerprint

import com.facebook.react.bridge.Arguments
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.Constants
import com.onegini.mobile.sdk.reactnative.mapers.UserProfileMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FingerprintAuthenticationEventEmitter @Inject constructor(private val deviceEventEmitter: DeviceEventManagerModule.RCTDeviceEventEmitter) {
    fun startAuthentication(user: UserProfile) {
        val map = Arguments.createMap()
        UserProfileMapper.add(map, user)
        map.putString("action", Constants.FINGERPRINT_NOTIFICATION_START_AUTHENTICATION)
        deviceEventEmitter.emit(Constants.ONEWELCOME_FINGERPRINT_NOTIFICATION, map)
    }

    fun onNextAuthenticationAttempt() {
        val map = Arguments.createMap()
        map.putString("action", Constants.FINGERPRINT_NOTIFICATION_ON_NEXT_AUTHENTICATION_ATTEMPT)
        deviceEventEmitter.emit(Constants.ONEWELCOME_FINGERPRINT_NOTIFICATION, map)
    }

    fun onFingerprintCaptured() {
        val map = Arguments.createMap()
        map.putString("action", Constants.FINGERPRINT_NOTIFICATION_ON_FINGERPRINT_CAPTURED)
        deviceEventEmitter.emit(Constants.ONEWELCOME_FINGERPRINT_NOTIFICATION, map)
    }

    fun finishAuthentication() {
        val map = Arguments.createMap()
        map.putString("action", Constants.FINGERPRINT_NOTIFICATION_FINISH_AUTHENTICATION)
        deviceEventEmitter.emit(Constants.ONEWELCOME_FINGERPRINT_NOTIFICATION, map)
    }
}
