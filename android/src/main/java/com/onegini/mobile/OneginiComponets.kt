package com.onegini.mobile

import android.content.Context
import com.onegini.mobile.storage.UserStorage
import com.onegini.mobile.util.DeregistrationUtil

object OneginiComponets {

    lateinit var oneginiSDK: OneginiSDK
    lateinit var deregistrationUtil: DeregistrationUtil
    lateinit var userStorage: UserStorage

    fun init(appContext: Context) {
        oneginiSDK = OneginiSDK(appContext)
        deregistrationUtil = DeregistrationUtil(appContext)
        userStorage = UserStorage(appContext)
    }
}