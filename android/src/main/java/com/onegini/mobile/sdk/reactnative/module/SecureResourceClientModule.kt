package com.onegini.mobile.sdk.reactnative.module

import com.onegini.mobile.sdk.android.client.OneginiClient
import com.onegini.mobile.sdk.reactnative.Constants.ANONYMOUS_SERVICE
import com.onegini.mobile.sdk.reactnative.Constants.IMPLICIT_USER_SERVICE
import com.onegini.mobile.sdk.reactnative.Constants.USER_SERVICE
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.network.client.ResourcesClient
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Module
class SecureResourceClientModule
{

    @Provides
    @Singleton
    @Named(ANONYMOUS_SERVICE)
    fun provideAnonymousRetrofitClient(oneginiSDK: OneginiSDK): ResourcesClient {
        val okHttpClient: OkHttpClient = oneginiSDK.oneginiClient.deviceClient.anonymousResourceOkHttpClient
        return prepareSecuredRetrofitClient(oneginiSDK, okHttpClient)
    }

    @Provides
    @Singleton
    @Named(IMPLICIT_USER_SERVICE)
    fun provideImplicitRetrofitClient(oneginiSDK: OneginiSDK): ResourcesClient {
        val okHttpClient: OkHttpClient = oneginiSDK.oneginiClient.userClient.implicitResourceOkHttpClient
        return prepareSecuredRetrofitClient(oneginiSDK, okHttpClient)
    }

    @Provides
    @Singleton
    @Named(USER_SERVICE)
    fun provideUserRetrofitClient(oneginiSDK: OneginiSDK): ResourcesClient {
        val okHttpClient: OkHttpClient = oneginiSDK.oneginiClient.userClient.resourceOkHttpClient
        return prepareSecuredRetrofitClient(oneginiSDK, okHttpClient)
    }

    private fun prepareSecuredRetrofitClient(oneginiSDK: OneginiSDK, okHttpClient: OkHttpClient): ResourcesClient {
        val oneginiClient: OneginiClient = oneginiSDK.oneginiClient

        val retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(oneginiClient.configModel.resourceBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()

        return retrofit.create(ResourcesClient::class.java)
    }
}