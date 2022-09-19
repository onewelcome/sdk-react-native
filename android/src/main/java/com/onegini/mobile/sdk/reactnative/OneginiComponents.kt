package com.onegini.mobile.sdk.reactnative

import com.onegini.mobile.sdk.reactnative.network.client.SecureResourceClient
import com.facebook.react.bridge.ReactApplicationContext
// TODO: Refactor this to dependency injection RNP-96
object OneginiComponents {

    lateinit var oneginiSDK: OneginiSDK
    lateinit var secureResourceClient: SecureResourceClient
    lateinit var reactApplicationContext: ReactApplicationContext

    fun init(reactApplicationContext: ReactApplicationContext) {
        this.reactApplicationContext = reactApplicationContext
        oneginiSDK = OneginiSDK(reactApplicationContext)
        secureResourceClient = SecureResourceClient(oneginiSDK)
    }
}
