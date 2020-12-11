//@todo Later will be transferred to RN Wrapper
package com.onegini.mobile.managers

import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.model.rn.OneginiReactNativeConfig
import com.onegini.mobile.sdk.android.handlers.OneginiInitializationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiInitializationError
import com.onegini.mobile.sdk.android.handlers.error.OneginiInitializationError.InitializationErrorType
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.storage.UserStorage
import com.onegini.mobile.util.DeregistrationUtil
import com.onegini.mobile.view.handlers.InitializationHandler

class OneginiClientInitializer(private val oneginiSDK: OneginiSDK,
                               private val configModelClassName: String?,
                               private val securityControllerClassName: String?,
                               private val deregistrationUtil: DeregistrationUtil,
                               private val userStorage: UserStorage) {
    fun startOneginiClient(config: OneginiReactNativeConfig, initializationHandler: InitializationHandler) {
        if (!oneginiSDK.isInitialized) {
            start(config, initializationHandler)
        } else {
            initializationHandler.onSuccess()
        }
    }

    private fun start(config: OneginiReactNativeConfig, initializationHandler: InitializationHandler) {
        oneginiSDK.init(config, configModelClassName, securityControllerClassName)
        val oneginiClient = oneginiSDK.oneginiClient
        oneginiClient.start(object : OneginiInitializationHandler {
            override fun onSuccess(removedUserProfiles: Set<UserProfile>) {
                oneginiSDK.onStart()
                setInitialized()
                if (!removedUserProfiles.isEmpty()) {
                    removeUserProfiles(removedUserProfiles)
                }
                initializationHandler.onSuccess()
            }

            override fun onError(error: OneginiInitializationError) {
                @InitializationErrorType val errorType = error.errorType
                if (errorType == OneginiInitializationError.DEVICE_DEREGISTERED) {
                    deregistrationUtil.onDeviceDeregistered()
                }
                initializationHandler.onError(error.message)
            }

            private fun removeUserProfiles(removedUserProfiles: Set<UserProfile>) {
                for (userProfile in removedUserProfiles) {
                    userStorage.removeUser(userProfile)
                }
            }

            @Synchronized
            private fun setInitialized() {
                oneginiSDK.isInitialized = true
            }
        })
    }
}