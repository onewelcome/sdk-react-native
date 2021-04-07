package com.onegini.mobile.mapers

import android.util.Log
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.WritableMap
import com.google.gson.JsonObject
import com.onegini.mobile.model.ApplicationDetails
import java.lang.Exception

object JsonMapper {

    fun toWritableMap(json: JsonObject): String {
        // right now we send it as a String but maybe in the future we would need to 'handle' some fields here...
        return json.toString()
    }

}