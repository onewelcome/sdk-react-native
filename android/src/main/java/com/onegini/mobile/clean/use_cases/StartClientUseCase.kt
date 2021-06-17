package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.exception.OneginReactNativeException
import com.onegini.mobile.mapers.OneginiReactNativeConfigMapper
import com.onegini.mobile.model.rn.OneginiReactNativeConfig
import com.onegini.mobile.sdk.android.handlers.OneginiInitializationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiInitializationError
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.view.handlers.customregistration.CustomRegistrationObserver
import com.onegini.mobile.view.handlers.fingerprint.FingerprintAuthenticationObserver
import com.onegini.mobile.view.handlers.mobileauthotp.MobileAuthOtpRequestObserver
import com.onegini.mobile.view.handlers.pins.PinNotificationObserver
import java.lang.Exception

class StartClientUseCase(private val oneginiSDK: OneginiSDK, private val reactApplicationContext: ReactApplicationContext) {

    operator fun invoke(rnConfig: ReadableMap, promise: Promise) {

        var config: OneginiReactNativeConfig

        try {
            config = OneginiReactNativeConfigMapper.toOneginiReactNativeConfig(rnConfig)
        } catch (e: Exception) {
            promise.reject(OneginReactNativeException.WRONG_CONFIG_MODEL.toString(), "Provided config model parameters are wrong")
            return
        }

        try {
            oneginiSDK.init(config)
        } catch (e: Exception) {
            promise.reject(OneginReactNativeException.WRONG_CONFIG_MODEL.toString(), "Configuration error. Did you provide OneginiClientConfigModel?")
            return
        }

        oneginiSDK.oneginiClient.start(object : OneginiInitializationHandler {
            override fun onSuccess(removedUserProfiles: Set<UserProfile>) {
                oneginiSDKInitiated()

                promise.resolve(null)
            }

            override fun onError(error: OneginiInitializationError) {
                promise.reject(error.errorType.toString(), error.message)
            }
        })
    }

    private fun oneginiSDKInitiated() {
        oneginiSDK.setPinNotificationObserver(PinNotificationObserver(reactApplicationContext))
        oneginiSDK.setCustomRegistrationObserver(CustomRegistrationObserver(reactApplicationContext))
        oneginiSDK.setMobileAuthOtpRequestObserver(MobileAuthOtpRequestObserver(reactApplicationContext))
        oneginiSDK.setFingerprintAuthenticationObserver(FingerprintAuthenticationObserver(reactApplicationContext))
    }
}
