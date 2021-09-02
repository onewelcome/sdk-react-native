package com.onegini.mobile.sdk.reactnative.mapers

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.onegini.mobile.sdk.android.model.entity.CustomInfo

object CustomInfoMapper {

    const val CUSTOM_INFO = "customInfo"

    fun toWritableMap(customInfo: CustomInfo?): WritableMap {
        val map = Arguments.createMap()
        if (customInfo != null) {
            map.putString("data", customInfo.data)
            map.putInt("status", customInfo.status)
        }
        return map
    }

    fun add(writableMap: WritableMap, customInfo: CustomInfo?) {
        writableMap.putMap(CUSTOM_INFO, customInfo?.let { toWritableMap(it) })
    }
}
