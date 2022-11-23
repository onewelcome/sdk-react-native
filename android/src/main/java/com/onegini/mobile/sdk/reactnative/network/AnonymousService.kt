package com.onegini.mobile.sdk.reactnative.network

import com.google.gson.JsonObject
import com.onegini.mobile.sdk.reactnative.Constants
import com.onegini.mobile.sdk.reactnative.model.ResourceRequestDetails
import com.onegini.mobile.sdk.reactnative.network.client.ResourcesClient
import dagger.Lazy
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class AnonymousService @Inject constructor(@Named(Constants.ANONYMOUS_SERVICE) private val anonymousRetrofitClient: Lazy<ResourcesClient>) {
    fun getResource(requestDetails: ResourceRequestDetails): Single<JsonObject> {
        val apiCall = when (requestDetails.method) {
            ApiCall.GET -> anonymousRetrofitClient.get().getResourcesDetails(requestDetails.path, requestDetails.headers)
            ApiCall.POST -> anonymousRetrofitClient.get().postResourcesDetails(requestDetails.path, requestDetails.headers, requestDetails.parameters)
            ApiCall.PUT -> anonymousRetrofitClient.get().putResourcesDetails(requestDetails.path, requestDetails.headers, requestDetails.parameters)
            ApiCall.DELETE -> anonymousRetrofitClient.get().deleteResourcesDetails(requestDetails.path, requestDetails.headers, requestDetails.parameters)
        }

        return apiCall
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
