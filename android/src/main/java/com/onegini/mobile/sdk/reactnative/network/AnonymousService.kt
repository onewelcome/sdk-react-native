/*
 * Copyright (c) 2016-2018 Onegini B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.onegini.mobile.sdk.reactnative.network

import com.google.gson.JsonObject
import com.onegini.mobile.sdk.reactnative.OneginiComponets
import com.onegini.mobile.sdk.reactnative.model.ResourceRequestDetails
import com.onegini.mobile.sdk.reactnative.network.client.ResourcesClient
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AnonymousService private constructor() {

    private val applicationDetailsRetrofitClient: ResourcesClient = OneginiComponets.secureResourceClient.prepareSecuredAnonymousRetrofitClient(ResourcesClient::class.java)

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
        private var INSTANCE: AnonymousService? = null
        fun getInstance(): AnonymousService {
            if (INSTANCE == null) {
                INSTANCE = AnonymousService()
            }
            return INSTANCE!!
        }
    }
}
