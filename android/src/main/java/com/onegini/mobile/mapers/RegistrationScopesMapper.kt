package com.onegini.mobile.mapers

import com.facebook.react.bridge.ReadableArray

object RegistrationScopesMapper {

    fun toStringArray(scopes: ReadableArray): Array<String> {
        return scopes.toArrayList()
                .filterIsInstance<String>()
                .toTypedArray()
    }
}
