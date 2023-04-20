package com.onegini.mobile.sdk.reactnative.mapers

import com.google.gson.JsonObject

object JsonMapper {

    fun toWritableMap(json: JsonObject): String {
        // right now we send it as a String but maybe in the future we would need to 'handle' some fields here...
        return json.toString()
    }
}
