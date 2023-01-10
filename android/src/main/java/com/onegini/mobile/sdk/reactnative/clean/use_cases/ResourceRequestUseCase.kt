package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.PARAMETERS_NOT_CORRECT
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.RESOURCE_CALL_ERROR
import com.onegini.mobile.sdk.reactnative.exception.REQUEST_TYPE_NOT_SUPPORTED
import com.onegini.mobile.sdk.reactnative.mapers.ResourceRequestDetailsMapper
import com.onegini.mobile.sdk.reactnative.model.ResourceRequestDetails
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceRequestUseCase @Inject constructor(
    private val oneginiSDK: OneginiSDK
) {

    operator fun invoke(type: String, details: ReadableMap, promise: Promise) {
        try {
            val resourceClient = mapTypeToResourceClient(type)
            val requestDetails = ResourceRequestDetailsMapper.toResourceRequestDetails(details)
            performResourceRequest(resourceClient, requestDetails, promise)
        } catch (error: OneginiReactNativeException) {
            return promise.reject(error.errorType.toString(), error.message)
        }
    }

    private fun performResourceRequest(resourceClient: OkHttpClient, requestDetails: ResourceRequestDetails, promise: Promise) {
        // FIXME: RNP-138 We will need to expose a method to get the resourceBaseUrl in the RN side and then allow passing a full url here.
        // FIXME: RNP-140: Support adding a body for requests that are not GET requests
        // FIXME: RNP-128: Support Formdata requests
        try {
            val request = Request.Builder()
                .url(requestDetails.path)
                .headers(requestDetails.headers)
                .build()

            resourceClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    promise.reject(RESOURCE_CALL_ERROR.code.toString(), e.message)
                }

                override fun onResponse(call: Call, response: Response) {
                    promise.resolve(response.body?.string())
                }
            })
        } catch (error: IllegalArgumentException) {
            promise.reject(PARAMETERS_NOT_CORRECT.code.toString(), error.message)
        }
    }

    private fun mapTypeToResourceClient(type: String): OkHttpClient {
        return when (type) {
            "User" -> oneginiSDK.oneginiClient.userClient.resourceOkHttpClient
            "ImplicitUser" -> oneginiSDK.oneginiClient.userClient.implicitResourceOkHttpClient
            "Anonymous" -> oneginiSDK.oneginiClient.deviceClient.anonymousResourceOkHttpClient
            else -> throw OneginiReactNativeException(PARAMETERS_NOT_CORRECT.code, REQUEST_TYPE_NOT_SUPPORTED)
        }
    }
}
