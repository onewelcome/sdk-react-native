package com.onegini.mobile.sdk.reactnative.mapers

import com.facebook.react.bridge.ReadableMap
import com.onegini.mobile.sdk.reactnative.model.ResourceRequestDetails
import com.onegini.mobile.sdk.reactnative.network.ApiCall

object ResourceRequestDetailsMapper {

    fun toResourceRequestDetails(map: ReadableMap): ResourceRequestDetails {

        val headers: MutableMap<String, String> = mutableMapOf()
        val parameters: MutableMap<String, String> = mutableMapOf()

        map.getMap("headers")?.entryIterator?.forEach {
            headers[it.key] = it.value.toString()
        }

        map.getMap("parameters")?.entryIterator?.forEach {
            parameters[it.key] = it.value.toString()
        }

        val method = try {
            ApiCall.valueOf(map.getString("method") ?: "GET")
        } catch (e: IllegalArgumentException) {
            ApiCall.GET
        }

        return ResourceRequestDetails(
            path = map.getString("path") ?: "/",
            method = method,
            encoding = map.getString("encoding") ?: "application/json",
            headers = headers,
            parameters = parameters
        )
    }
}
