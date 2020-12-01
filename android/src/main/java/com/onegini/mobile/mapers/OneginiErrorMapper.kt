package com.onegini.mobile.mapers

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.onegini.mobile.sdk.android.handlers.error.OneginiError
import com.onegini.mobile.sdk.android.handlers.error.OneginiErrorDetails

object OneginiErrorMapper {

    fun toWritableMap(oneginiError: OneginiError?): WritableMap {
        val map = Arguments.createMap()
        if (oneginiError == null) {
            return map
        }
        map.putInt("errorType", oneginiError.errorType)
        map.putMap("errorDetails", toWritableMap(oneginiError.errorDetails))
        return map
    }

    fun toWritableMap(details: OneginiErrorDetails?): WritableMap {
        val map = Arguments.createMap()
        if (details == null) {
            return map
        }
        UserProfileMapper.add(map, details.userProfile)
        CustomInfoMapper.add(map, details.customInfo)
        OneginiAuthenticatorMapper.add(map, details.authenticator)
        return map
    }
}