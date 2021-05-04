package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import com.onegini.mobile.OneginiComponets
import com.onegini.mobile.mapers.ResourceRequestDetailsMapper
import com.onegini.mobile.model.ResourceRequestDetails
import com.onegini.mobile.network.ApiCall
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.*

class ResourceRequestUseCase {

    private var disposable: Disposable? = null

    operator fun invoke(type: String, details: ReadableMap, promise: Promise) {
        val requestDetails = ResourceRequestDetailsMapper.toResourceRequestDetails(details)
        val client = getClient(type)
        val baseUrl = OneginiComponets.oneginiSDK.oneginiClient.configModel.resourceBaseUrl
        val request = prepareRequest(baseUrl, requestDetails)

        disposable = makeRequest(client, request, promise)
    }

    fun dispose() {
        disposable?.dispose()
    }

    //

    private fun getClient(type: String): OkHttpClient {
        return when (type) {
            "User" -> OneginiComponets.oneginiSDK.oneginiClient.userClient.resourceOkHttpClient
            "ImplicitUser" -> OneginiComponets.oneginiSDK.oneginiClient.userClient.implicitResourceOkHttpClient
            "Anonymous" -> OneginiComponets.oneginiSDK.oneginiClient.deviceClient.anonymousResourceOkHttpClient
            else -> OneginiComponets.oneginiSDK.oneginiClient.deviceClient.anonymousResourceOkHttpClient
        }
    }

    private fun makeRequest(okHttpClient: OkHttpClient, request: Request, promise: Promise): Disposable {
        return Observable.fromCallable { okHttpClient.newCall(request).execute() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { data ->
                    val response = data?.body()?.string() ?: "{}"
                    promise.resolve(response)
                },
                { error ->
                    // should we create custom resource request error?
                    promise.reject(error.message, error.stackTrace.toString())
                }
            )
    }

    private fun prepareRequest(baseUrl: String, requestDetails: ResourceRequestDetails): Request {
        val request = Request.Builder()
        if (requestDetails.body.isNotEmpty() && requestDetails.method != ApiCall.GET) {
            val createdBody = RequestBody.create(MediaType.parse(requestDetails.encoding), requestDetails.body)
            request.method(requestDetails.method.name, createdBody)
        }

        val url = "$baseUrl${requestDetails.path}"
        request.url(url)

        if (requestDetails.headers.isNotEmpty()) {
            try {
                request.headers(Headers.of(requestDetails.headers))
            } catch (error: Exception) {
            }
        }

        return request.build()
    }
}
