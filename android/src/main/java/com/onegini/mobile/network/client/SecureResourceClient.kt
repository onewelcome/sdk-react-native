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
package com.onegini.mobile.network.client

import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.sdk.android.client.OneginiClient
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class SecureResourceClient(val oneginiSDK: OneginiSDK) {
    fun <T> prepareSecuredUserRetrofitClient(clazz: Class<T>): T {
        val okHttpClient: OkHttpClient = oneginiSDK.oneginiClient.userClient.resourceOkHttpClient
        return prepareSecuredRetrofitClient(clazz, okHttpClient)
    }

    fun <T> prepareSecuredImplicitUserRetrofitClient(clazz: Class<T>): T {
        val okHttpClient: OkHttpClient = oneginiSDK.oneginiClient.userClient.implicitResourceOkHttpClient
        return prepareSecuredRetrofitClient(clazz, okHttpClient)
    }

    fun <T> prepareSecuredAnonymousRetrofitClient(clazz: Class<T>): T {
        val okHttpClient: OkHttpClient = oneginiSDK.oneginiClient.deviceClient.anonymousResourceOkHttpClient
        return prepareSecuredRetrofitClient(clazz, okHttpClient)
    }

    private fun <T> prepareSecuredRetrofitClient(clazz: Class<T>, okHttpClient: OkHttpClient): T {
        val oneginiClient: OneginiClient = oneginiSDK.oneginiClient
        val retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(oneginiClient.configModel.resourceBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        return retrofit.create(clazz)
    }
}