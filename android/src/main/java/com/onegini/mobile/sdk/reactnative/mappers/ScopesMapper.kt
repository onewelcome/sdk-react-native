package com.onegini.mobile.sdk.reactnative.mapers

import com.facebook.react.bridge.ReadableArray

object ScopesMapper {

    fun toStringArray(scopes: ReadableArray?): Array<String> {
        scopes ?: return arrayOf()
        return scopes.toArrayList()
            .filterIsInstance<String>()
            .toTypedArray()
    }
}
