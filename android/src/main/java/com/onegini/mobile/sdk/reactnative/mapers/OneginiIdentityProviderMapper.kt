package com.onegini.mobile.sdk.reactnative.mapers

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap
import com.onegini.mobile.sdk.android.model.OneginiIdentityProvider

object OneginiIdentityProviderMapper {

    const val IDENTITY_PROVIDER = "identityProvider"

    fun toWritableMap(provider: OneginiIdentityProvider): WritableMap {
        val map = Arguments.createMap()
        map.putString("id", provider.id)
        map.putString("name", provider.name)
        return map
    }

    fun toWritableMap(profiles: Set<OneginiIdentityProvider>?): WritableArray {
        val array = Arguments.createArray()
        profiles?.forEach {
            array.pushMap(toWritableMap(it))
        }
        return array
    }

    fun add(writableMap: WritableMap, profileUser: OneginiIdentityProvider?) {
        writableMap.putMap(IDENTITY_PROVIDER, profileUser?.let { toWritableMap(it) })
    }

    fun add(writableMap: WritableMap, identityProviderId: String) {
        writableMap.putString("identityProviderId", identityProviderId)
    }
}
