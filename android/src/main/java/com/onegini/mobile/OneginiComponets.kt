package com.onegini.mobile

import android.content.Context

object OneginiComponets {

    lateinit var oneginiSDK: OneginiSDK

    fun init(appContext: Context) {
        oneginiSDK = OneginiSDK(appContext)
    }
}
