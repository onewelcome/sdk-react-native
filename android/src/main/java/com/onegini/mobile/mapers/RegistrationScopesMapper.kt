package com.onegini.mobile.mapers

import com.facebook.react.bridge.ReadableArray

object RegistrationScopesMapper {

    fun toStringArray(scopes: ReadableArray): Array<String> {
        val array = ArrayList<String>()

        scopes.toArrayList().forEach {
            if (it is String) {
                array.add(it)
            }
        }

        return array.toTypedArray()
    }
}
