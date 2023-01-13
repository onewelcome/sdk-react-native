package com.onegini.mobile.sdk.reactnative.mapers

import com.facebook.react.bridge.ReadableMap
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.PARAMETERS_NOT_CORRECT
import com.onegini.mobile.sdk.reactnative.exception.REQUEST_METHOD_NOT_SUPPORTED
import com.onegini.mobile.sdk.reactnative.exception.REQUEST_MISSING_PATH_PARAMETER
import com.onegini.mobile.sdk.reactnative.model.ResourceRequestDetails
import com.onegini.mobile.sdk.reactnative.network.ApiCall
import okhttp3.Headers

object ResourceRequestDetailsMapper {

    fun toResourceRequestDetails(map: ReadableMap): ResourceRequestDetails {

        val parameters: MutableMap<String, String> = mutableMapOf()
        val headerBuilder = Headers.Builder()

        // TODO: RNP-140: Check if we need to set content-type to a default value with POST
        map.getMap("headers")?.entryIterator?.forEach {
            val headerValue = it.value
            if (headerValue is String) {
                headerBuilder.add(it.key, headerValue)
            }
        }

        // FIXME: RNP-140: Remove this as part of supporting body
        map.getMap("parameters")?.entryIterator?.forEach {
            parameters[it.key] = it.value.toString()
        }

        val method = try {
            ApiCall.valueOf(map.getString("method") ?: "GET")
        } catch (e: IllegalArgumentException) {
            throw OneginiReactNativeException(PARAMETERS_NOT_CORRECT.code, REQUEST_METHOD_NOT_SUPPORTED)
        }

        return ResourceRequestDetails(
            path = map.getString("path") ?: throw OneginiReactNativeException(PARAMETERS_NOT_CORRECT.code, REQUEST_MISSING_PATH_PARAMETER),
            method = method,
            headers = headerBuilder.build(),
            parameters = parameters
        )
    }
}
