package com.onegini.mobile

import android.content.Context
import com.onegini.mobile.network.client.SecureResourceClient

object OneginiComponets {

    lateinit var oneginiSDK: OneginiSDK
    lateinit var secureResourceClient: SecureResourceClient

    fun init(appContext: Context) {
        oneginiSDK = OneginiSDK(appContext)
        secureResourceClient = SecureResourceClient(oneginiSDK)
    }
}
