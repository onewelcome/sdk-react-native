package com.onegini.mobile.sdk.reactnative.mapers

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.onegini.mobile.sdk.reactnative.model.ApplicationDetails

object ApplicationDetailsMapper {

    const val APPLICATION_DETAILS = "applicationDetails"

    fun toWritableMap(details: ApplicationDetails?): WritableMap {
        val map = Arguments.createMap()
        if (details != null) {
            map.putString("applicationIdentifier", details.applicationIdentifier)
            map.putString("applicationPlatform", details.applicationPlatform)
            map.putString("applicationVersion", details.applicationVersion)
        }
        return map
    }

    fun add(writableMap: WritableMap, details: ApplicationDetails?) {
        writableMap.putMap(APPLICATION_DETAILS, details?.let { toWritableMap(it) })
    }
}
