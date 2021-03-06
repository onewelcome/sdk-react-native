package com.onegini.mobile.mapers

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.onegini.mobile.sdk.android.handlers.error.OneginiError

object OneginiErrorMapper {

    fun toWritableMap(oneginiError: OneginiError?): WritableMap {
        val map = Arguments.createMap()
        if (oneginiError == null) {
            return map
        }
        map.putInt("errorType", oneginiError.errorType)
        map.putString("errorType", oneginiError.message)
        return map
    }
}