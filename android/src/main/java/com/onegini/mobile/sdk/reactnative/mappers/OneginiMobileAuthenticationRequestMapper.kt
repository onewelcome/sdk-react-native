package com.onegini.mobile.sdk.reactnative.mapers

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.onegini.mobile.sdk.android.model.entity.OneginiMobileAuthenticationRequest

object OneginiMobileAuthenticationRequestMapper {

    const val MOBILE_AUTHENTICATION_REQUEST = "mobileAuthenticationRequest"

    fun toWritableMap(request: OneginiMobileAuthenticationRequest): WritableMap {
        val map = Arguments.createMap()
        map.putString("message", request.message)
        map.putString("type", request.type)
        map.putString("transactionId", request.transactionId)
        map.putString("signingData", request.signingData)
        UserProfileMapper.add(map, request.userProfile)
        return map
    }

    fun add(writableMap: WritableMap, request: OneginiMobileAuthenticationRequest?) {
        writableMap.putMap(MOBILE_AUTHENTICATION_REQUEST, request?.let { toWritableMap(it) })
    }
}
