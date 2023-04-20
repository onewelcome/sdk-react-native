package com.onegini.mobile.sdk.reactnative.mapers

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.onegini.mobile.sdk.android.model.OneginiAppToWebSingleSignOn

object OneginiAppToWebSingleSignOnMapper {

    const val APP_TO_WEB_SINGLE_SIGNON = "appToWebSingleSignOn"

    fun toWritableMap(signOn: OneginiAppToWebSingleSignOn): WritableMap {
        val map = Arguments.createMap()
        map.putString("token", signOn.token)
        map.putString("url", signOn.redirectUrl.toString())
        return map
    }

    fun add(writableMap: WritableMap, request: OneginiAppToWebSingleSignOn?) {
        writableMap.putMap(APP_TO_WEB_SINGLE_SIGNON, request?.let { toWritableMap(it) })
    }
}
