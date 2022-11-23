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
class ImplicitUserService @Inject constructor(@Named(Constants.IMPLICIT_USER_SERVICE) private val implicitRetrofitClient: Lazy<ResourcesClient>){

    fun getResource(requestDetails: ResourceRequestDetails): Single<JsonObject> {

        val apiCall = when (requestDetails.method) {
            ApiCall.GET -> implicitRetrofitClient.get().getResourcesDetails(requestDetails.path, requestDetails.headers)
            ApiCall.POST -> implicitRetrofitClient.get().postResourcesDetails(requestDetails.path, requestDetails.headers, requestDetails.parameters)
            ApiCall.PUT -> implicitRetrofitClient.get().putResourcesDetails(requestDetails.path, requestDetails.headers, requestDetails.parameters)
            ApiCall.DELETE -> implicitRetrofitClient.get().deleteResourcesDetails(requestDetails.path, requestDetails.headers, requestDetails.parameters)
        }

        return apiCall
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
