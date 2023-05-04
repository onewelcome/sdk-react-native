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
    Arguments.createMap().apply {
      UserProfileMapper.add(this, user)
      putString(ACTION, Constants.FINGERPRINT_NOTIFICATION_START_AUTHENTICATION)
    }.also { dataMap ->
      deviceEventEmitter.emit(Constants.ONEWELCOME_FINGERPRINT_NOTIFICATION, dataMap)
    }
  }

  fun onNextAuthenticationAttempt() {
    Arguments.createMap().apply {
      putString(ACTION, Constants.FINGERPRINT_NOTIFICATION_ON_NEXT_AUTHENTICATION_ATTEMPT)
    }.also { dataMap ->
      deviceEventEmitter.emit(Constants.ONEWELCOME_FINGERPRINT_NOTIFICATION, dataMap)
    }
  }

  fun onFingerprintCaptured() {
    Arguments.createMap().apply {
      putString(ACTION, Constants.FINGERPRINT_NOTIFICATION_ON_FINGERPRINT_CAPTURED)
    }.also { dataMap ->
      deviceEventEmitter.emit(Constants.ONEWELCOME_FINGERPRINT_NOTIFICATION, dataMap)
    }
  }

  fun finishAuthentication() {
    Arguments.createMap().apply {
      putString(ACTION, Constants.FINGERPRINT_NOTIFICATION_FINISH_AUTHENTICATION)
    }.also { dataMap ->
      deviceEventEmitter.emit(Constants.ONEWELCOME_FINGERPRINT_NOTIFICATION, dataMap)
    }
  }

  companion object {
    private const val ACTION = "action"
  }
}
