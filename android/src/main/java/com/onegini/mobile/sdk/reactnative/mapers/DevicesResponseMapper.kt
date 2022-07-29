package com.onegini.mobile.sdk.reactnative.mapers

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap
import com.onegini.mobile.sdk.reactnative.model.Device
import com.onegini.mobile.sdk.reactnative.network.response.DevicesResponse

object DevicesResponseMapper {

    const val DEVICES_RESPONSE = "devicesResponse"

    fun toWritableMap(details: DevicesResponse?): WritableMap {
        val map = Arguments.createMap()
        if (details != null) {
            map.putArray("devices", toWritableArray(details.devices))
        }
        return map
    }

    fun toWritableArray(devices: List<Device>?): WritableArray {
        val array = Arguments.createArray()
        devices?.forEach {
            array.pushMap(toWritableMap(it))
        }
        return array
    }

    fun toWritableMap(device: Device?): WritableMap {
        val map = Arguments.createMap()
        if (device != null) {
            map.putString("id", device.id)
            map.putString("name", device.name)
            map.putString("application", device.application)
            map.putString("platform", device.platform)
            map.putBoolean("isMobileAuthenticationEnabled", device.isMobileAuthenticationEnabled)
        }
        return map
    }

    fun add(writableMap: WritableMap, details: DevicesResponse?) {
        writableMap.putMap(DEVICES_RESPONSE, details?.let { toWritableMap(it) })
    }
}
