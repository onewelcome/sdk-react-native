package com.onegini.mobile.sdk.reactnative

import com.onegini.mobile.sdk.reactnative.network.client.SecureResourceClient
// TODO: Refactor this to dependency injection RNP-96
object OneginiComponents {

    lateinit var oneginiSDK: OneginiSDK
    lateinit var secureResourceClient: SecureResourceClient

    fun init(oneginiSDK: OneginiSDK) {
        this.oneginiSDK = oneginiSDK
        secureResourceClient = SecureResourceClient(oneginiSDK)
    }
}
