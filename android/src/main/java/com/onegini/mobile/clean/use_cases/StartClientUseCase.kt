package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.onegini.mobile.OneginiComponets
import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.clean.model.SdkError
import com.onegini.mobile.managers.OneginiClientInitializer
import com.onegini.mobile.mapers.OneginiReactNativeConfigMapper
import com.onegini.mobile.sdk.android.handlers.error.OneginiInitializationError
import com.onegini.mobile.view.handlers.InitializationHandler
import com.onegini.mobile.view.handlers.customregistration.CustomRegistrationObserver
import com.onegini.mobile.view.handlers.fingerprint.FingerprintAuthenticationObserver
import com.onegini.mobile.view.handlers.mobileauthotp.MobileAuthOtpRequestObserver
import com.onegini.mobile.view.handlers.pins.PinNotificationObserver
import java.lang.Exception


class StartClientUseCase(val oneginiSDK: OneginiSDK, val reactApplicationContext: ReactApplicationContext) {

    operator fun invoke(rnConfig: ReadableMap, promise: Promise) {
        try {
            val config = OneginiReactNativeConfigMapper.toOneginiReactNativeConfig(rnConfig)

            val oneginiClientInitializer = OneginiClientInitializer(
                    OneginiComponets.oneginiSDK)

            oneginiClientInitializer.startOneginiClient(config, object : InitializationHandler {
                override fun onSuccess() {
                    oneginiSDKInitiated()

                    promise.resolve(null)
                }

                override fun onError(error: OneginiInitializationError) {
                    promise.reject(error.errorType.toString(), error.message
                            ?: "no message")
                }
            })
        } catch (e: Exception) {
            promise.reject("Exception", "")
        }
    }

    //

    private fun oneginiSDKInitiated() {
        oneginiSDK.setPinNotificationObserver(PinNotificationObserver(reactApplicationContext))
        oneginiSDK.setCustomRegistrationObserver(CustomRegistrationObserver(reactApplicationContext))
        oneginiSDK.setMobileAuthOtpRequestObserver(MobileAuthOtpRequestObserver(reactApplicationContext))
        oneginiSDK.setFingerprintAuthenticationObserver(FingerprintAuthenticationObserver(reactApplicationContext))
    }
}