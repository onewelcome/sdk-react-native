package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.onegini.mobile.sdk.android.handlers.OneginiInitializationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiInitializationError
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.reactnative.mapers.OneginiReactNativeConfigMapper
import com.onegini.mobile.sdk.reactnative.model.rn.OneginiReactNativeConfig

class StartClientUseCase(
    private val oneginiSDK: OneginiSDK,
) {
    operator fun invoke(rnConfig: ReadableMap, promise: Promise) {

        var config: OneginiReactNativeConfig

        try {
            config = OneginiReactNativeConfigMapper.toOneginiReactNativeConfig(rnConfig)
        } catch (e: Exception) {
            promise.reject(OneginiWrapperErrors.WRONG_CONFIG_MODEL.code, OneginiWrapperErrors.WRONG_CONFIG_MODEL.message)
            return
        }

        try {
            oneginiSDK.init(config)
        } catch (e: Exception) {
            promise.reject(OneginiWrapperErrors.WRONG_CONFIG_MODEL.code, OneginiWrapperErrors.WRONG_CONFIG_MODEL.message)
            return
        }

        oneginiSDK.oneginiClient.start(object : OneginiInitializationHandler {
            override fun onSuccess(removedUserProfiles: Set<UserProfile>) {
                promise.resolve(null)
            }

            override fun onError(error: OneginiInitializationError) {
                promise.reject(error.errorType.toString(), error.message)
            }
        })
    }
}
