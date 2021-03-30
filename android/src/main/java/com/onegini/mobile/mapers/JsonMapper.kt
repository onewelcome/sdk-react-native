package com.onegini.mobile.mapers

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.google.gson.JsonObject
import com.onegini.mobile.model.ApplicationDetails

object JsonMapper {


    fun toWritableMap(json: JsonObject): WritableMap {
        val map = Arguments.createMap()

        json.entrySet().forEach {
            map.putString(it.key, it.value.asString)
        }

        return map
    }

}