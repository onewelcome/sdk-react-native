package com.onegini.mobile.sdk.reactnative.mapers

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.onegini.mobile.sdk.reactnative.model.ImplicitUserDetails

object ImplicitUserDetailsMapper {

    const val IMPLICIT_USER_DETAILS = "implicitUserDetails"

    fun toWritableMap(details: ImplicitUserDetails): WritableMap {
        val map = Arguments.createMap()
        map.putString("decoratedUserId", details.toString())
        return map
    }

    fun add(writableMap: WritableMap, details: ImplicitUserDetails?) {
        writableMap.putMap(IMPLICIT_USER_DETAILS, details?.let { toWritableMap(it) })
    }
}
