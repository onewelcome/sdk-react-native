// @todo Later will be transferred to RN Wrapper
package com.onegini.mobile.managers

import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.model.rn.OneginiReactNativeConfig
import com.onegini.mobile.sdk.android.handlers.OneginiInitializationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiInitializationError
import com.onegini.mobile.sdk.android.handlers.error.OneginiInitializationError.InitializationErrorType
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.view.handlers.InitializationHandler

class OneginiClientInitializer(private val oneginiSDK: OneginiSDK) {
    fun startOneginiClient(config: OneginiReactNativeConfig, initializationHandler: InitializationHandler) {
        if (!oneginiSDK.isInitialized) {
            start(config, initializationHandler)
        } else {
            initializationHandler.onSuccess()
        }
    }

    private fun start(config: OneginiReactNativeConfig, initializationHandler: InitializationHandler) {
        oneginiSDK.init(config)
        val oneginiClient = oneginiSDK.oneginiClient
        oneginiClient.start(object : OneginiInitializationHandler {
            override fun onSuccess(removedUserProfiles: Set<UserProfile>) {
                setInitialized()
                initializationHandler.onSuccess()
            }

            override fun onError(error: OneginiInitializationError) {
                @InitializationErrorType val errorType = error.errorType
                initializationHandler.onError(error)
            }

            @Synchronized
            private fun setInitialized() {
                oneginiSDK.isInitialized = true
            }
        })
    }
}
