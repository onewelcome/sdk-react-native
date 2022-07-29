package com.onegini.mobile.sdk.reactnative.mapers

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap
import com.onegini.mobile.sdk.android.model.OneginiAuthenticator

object OneginiAuthenticatorMapper {

    const val ONEGINI_AUTHENTICATOR = "oneginiAuthenticator"

    fun toWritableMap(authenticator: OneginiAuthenticator?): WritableMap {
        val map = Arguments.createMap()
        if (authenticator == null) {
            return map
        }
        map.putString("id", authenticator.id)
        map.putString("name", authenticator.name)
        map.putInt("type", authenticator.type)
        map.putBoolean("isPreferred", authenticator.isPreferred)
        map.putBoolean("isRegistered", authenticator.isRegistered)
        UserProfileMapper.add(map, authenticator.userProfile)
        return map
    }

    fun toWritableMap(authenticators: Set<OneginiAuthenticator>?): WritableArray {
        val array = Arguments.createArray()
        authenticators?.forEach {
            array.pushMap(toWritableMap(it))
        }
        return array
    }

    fun add(writableMap: WritableMap, authenticator: OneginiAuthenticator?) {
        writableMap.putMap(ONEGINI_AUTHENTICATOR, authenticator?.let { toWritableMap(it) })
    }
}
