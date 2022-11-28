package com.onegini.mobile.sdk.reactnative.handlers.customregistration

import com.facebook.react.bridge.Arguments
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.onegini.mobile.sdk.android.model.entity.CustomInfo
import com.onegini.mobile.sdk.reactnative.Constants
import com.onegini.mobile.sdk.reactnative.mapers.CustomInfoMapper
import com.onegini.mobile.sdk.reactnative.mapers.OneginiIdentityProviderMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomRegistrationEventEmitter @Inject constructor(private val deviceEventEmitter: DeviceEventManagerModule.RCTDeviceEventEmitter) {
     fun initRegistration(idProvider: String, info: CustomInfo?) {
        val map = Arguments.createMap()
        map.putString("action", Constants.CUSTOM_REGISTRATION_NOTIFICATION_INIT_REGISTRATION)
        OneginiIdentityProviderMapper.add(map, idProvider)
        CustomInfoMapper.add(map, info)
         deviceEventEmitter.emit(Constants.CUSTOM_REGISTRATION_NOTIFICATION, map)
    }

    fun finishRegistration(idProvider: String, info: CustomInfo?) {
        val map = Arguments.createMap()
        map.putString("action", Constants.CUSTOM_REGISTRATION_NOTIFICATION_FINISH_REGISTRATION)
        OneginiIdentityProviderMapper.add(map, idProvider)
        CustomInfoMapper.add(map, info)
        deviceEventEmitter.emit(Constants.CUSTOM_REGISTRATION_NOTIFICATION, map)
    }
}
