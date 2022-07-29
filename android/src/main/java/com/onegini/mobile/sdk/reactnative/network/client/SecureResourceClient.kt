package com.onegini.mobile.sdk.reactnative.network.client

import com.onegini.mobile.sdk.android.client.OneginiClient
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
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
        var okHttpClient: OkHttpClient = oneginiSDK.oneginiClient.deviceClient.anonymousResourceOkHttpClient
        return prepareSecuredRetrofitClient(clazz, okHttpClient)
    }

    private fun <T> prepareSecuredRetrofitClient(clazz: Class<T>, okHttpClient: OkHttpClient): T {
        val oneginiClient: OneginiClient = oneginiSDK.oneginiClient

        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(oneginiClient.configModel.resourceBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()

        return retrofit.create(clazz)
    }
}
