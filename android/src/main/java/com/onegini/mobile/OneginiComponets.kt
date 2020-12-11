package com.onegini.mobile

import android.content.Context
import com.onegini.mobile.network.client.SecureResourceClient
import com.onegini.mobile.storage.UserStorage
import com.onegini.mobile.util.DeregistrationUtil

object OneginiComponets {

    lateinit var oneginiSDK: OneginiSDK
    lateinit var deregistrationUtil: DeregistrationUtil
    lateinit var userStorage: UserStorage
    lateinit var secureResourceClient: SecureResourceClient

    fun init(appContext: Context) {
        oneginiSDK = OneginiSDK(appContext)
        deregistrationUtil = DeregistrationUtil(appContext)
        userStorage = UserStorage(appContext)
        secureResourceClient = SecureResourceClient(oneginiSDK)
    }
}