package com.onegini.mobile.sdk.reactnative

import android.content.Context
import com.onegini.mobile.sdk.reactnative.network.client.SecureResourceClient

object OneginiComponets {

    lateinit var oneginiSDK: OneginiSDK
    lateinit var secureResourceClient: SecureResourceClient

    fun init(appContext: Context) {
        oneginiSDK = OneginiSDK(appContext)
        secureResourceClient = SecureResourceClient(oneginiSDK)
    }
}
