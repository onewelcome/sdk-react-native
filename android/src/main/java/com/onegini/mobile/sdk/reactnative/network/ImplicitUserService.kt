package com.onegini.mobile.sdk.reactnative.network

import com.google.gson.JsonObject
import com.onegini.mobile.sdk.reactnative.OneginiComponents
import com.onegini.mobile.sdk.reactnative.model.ResourceRequestDetails
import com.onegini.mobile.sdk.reactnative.network.client.ResourcesClient
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class ImplicitUserService private constructor() {

    private val applicationDetailsRetrofitClient: ResourcesClient = OneginiComponents.secureResourceClient.prepareSecuredImplicitUserRetrofitClient(ResourcesClient::class.java)

    fun getResource(requestDetails: ResourceRequestDetails): Single<JsonObject> {
        val apiCall = when (requestDetails.method) {
            ApiCall.GET -> applicationDetailsRetrofitClient.getResourcesDetails(requestDetails.path, requestDetails.headers)
            ApiCall.POST -> applicationDetailsRetrofitClient.postResourcesDetails(requestDetails.path, requestDetails.headers, requestDetails.parameters)
            ApiCall.PUT -> applicationDetailsRetrofitClient.putResourcesDetails(requestDetails.path, requestDetails.headers, requestDetails.parameters)
            ApiCall.DELETE -> applicationDetailsRetrofitClient.deleteResourcesDetails(requestDetails.path, requestDetails.headers, requestDetails.parameters)
        }

        return apiCall
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    companion object {
        private var INSTANCE: ImplicitUserService? = null
        fun getInstance(): ImplicitUserService {
            if (INSTANCE == null) {
                INSTANCE = ImplicitUserService()
            }
            return INSTANCE!!
        }
    }
}
