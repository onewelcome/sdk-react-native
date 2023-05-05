package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import com.onegini.mobile.sdk.reactnative.Constants.RESOURCE_REQUEST_ANONYMOUS
import com.onegini.mobile.sdk.reactnative.Constants.RESOURCE_REQUEST_IMPLICIT_USER
import com.onegini.mobile.sdk.reactnative.Constants.RESOURCE_REQUEST_USER
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError.PARAMETERS_NOT_CORRECT
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError.RESOURCE_CALL_ERROR
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError.USER_NOT_AUTHENTICATED
import com.onegini.mobile.sdk.reactnative.exception.REQUEST_TYPE_NOT_SUPPORTED
import com.onegini.mobile.sdk.reactnative.exception.rejectRNException
import com.onegini.mobile.sdk.reactnative.mapers.ResourceRequestDetailsMapper
import com.onegini.mobile.sdk.reactnative.model.ResourceRequestDetails
import com.onegini.mobile.sdk.reactnative.network.ApiCall
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
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
      checkRequireAccessToken(type)
      performResourceRequest(resourceClient, requestDetails, promise)
    } catch (error: OneginiReactNativeException) {
      return promise.rejectRNException(error)
    }
  }

  private fun performResourceRequest(resourceClient: OkHttpClient, requestDetails: ResourceRequestDetails, promise: Promise) {
    try {
      val request = buildRequest(requestDetails)
      performCall(request, resourceClient, promise)
    } catch (error: IllegalArgumentException) {
      promise.reject(PARAMETERS_NOT_CORRECT.code.toString(), error.message)
    }
  }

  private fun buildRequest(requestDetails: ResourceRequestDetails): Request {
    return Request.Builder()
      .url(requestDetails.path)
      .headers(requestDetails.headers)
      .setMethod(requestDetails)
      .build()
  }

  private fun Request.Builder.setMethod(requestDetails: ResourceRequestDetails): Request.Builder {
    return when (requestDetails.method) {
      ApiCall.GET -> {
        this.get()
      }
      ApiCall.POST -> {
        val body = requestDetails.body ?: ""
        this.post(body.toRequestBody(null))
      }
      ApiCall.PUT -> {
        val body = requestDetails.body ?: ""
        this.put(body.toRequestBody(null))
      }
      ApiCall.DELETE -> {
        this.delete(requestDetails.body?.toRequestBody())
      }
    }
  }

  private fun performCall(request: Request, resourceClient: OkHttpClient, promise: Promise) {
    resourceClient.newCall(request).enqueue(object : Callback {
      override fun onFailure(call: Call, e: IOException) {
        promise.reject(RESOURCE_CALL_ERROR.code.toString(), e.message)
      }

      override fun onResponse(call: Call, response: Response) {
        Arguments.createMap().apply {
          putString("body", response.body?.string() ?: "")
          putMap("headers", headersToReadableMap(response.headers))
          putBoolean("ok", response.isSuccessful)
          putInt("status", response.code);
        }.also { resourceResponse ->
          promise.resolve(resourceResponse)
        }
      }
    })
  }

  private fun mapTypeToResourceClient(type: String): OkHttpClient {
    return when (type) {
      RESOURCE_REQUEST_USER -> oneginiSDK.oneginiClient.userClient.resourceOkHttpClient
      RESOURCE_REQUEST_IMPLICIT_USER -> oneginiSDK.oneginiClient.userClient.implicitResourceOkHttpClient
      RESOURCE_REQUEST_ANONYMOUS -> oneginiSDK.oneginiClient.deviceClient.anonymousResourceOkHttpClient
      else -> throw OneginiReactNativeException(PARAMETERS_NOT_CORRECT.code, REQUEST_TYPE_NOT_SUPPORTED)
    }
  }

  private fun headersToReadableMap(headers: Headers): ReadableMap {
    val result = Arguments.createMap()
    headers.forEach { header ->
      result.putString(header.first, header.second)
    }
    return result
  }

  // We do this check because iOS requires an accessToken to make an authenticated resource Call
  private fun checkRequireAccessToken(type: String) {
    if (oneginiSDK.oneginiClient.accessToken == null && type == RESOURCE_REQUEST_USER) {
      throw OneginiReactNativeException(USER_NOT_AUTHENTICATED)
    }
  }
}
