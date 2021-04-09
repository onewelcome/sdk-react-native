package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.ReadableMap
import com.onegini.mobile.OneginiComponets
import com.onegini.mobile.clean.model.SdkError
import com.onegini.mobile.managers.OneginiClientInitializer
import com.onegini.mobile.mapers.OneginiReactNativeConfigMapper
import com.onegini.mobile.sdk.android.handlers.error.OneginiInitializationError
import com.onegini.mobile.view.handlers.InitializationHandler
import java.lang.Exception


class StartClientUseCase {
    operator fun invoke(rnConfig: ReadableMap, onFinished: (success: Boolean, error: SdkError?) -> Unit) {
        try {
            val config = OneginiReactNativeConfigMapper.toOneginiReactNativeConfig(rnConfig)

            val oneginiClientInitializer = OneginiClientInitializer(
                    OneginiComponets.oneginiSDK)

            oneginiClientInitializer.startOneginiClient(config, object : InitializationHandler {
                override fun onSuccess() {
                    oneginiSDKInitiated()

                    onFinished(true, null)
                }

                override fun onError(error: OneginiInitializationError) {
                    onFinished(false, SdkError(error.errorType.toString(), error.message
                            ?: "no message"))
                }
            })
        } catch (e: Exception) {
            onFinished(false, SdkError("Exception", ""))
        }
    }

    //

    private fun oneginiSDKInitiated() {
//        OneginiComponets.oneginiSDK.setPinNotificationObserver(pinNotificationObserver)
//        OneginiComponets.oneginiSDK.setCustomRegistrationObserver(customRegistrationObserver)
//        OneginiComponets.oneginiSDK.setMobileAuthOtpRequestObserver(mobileAuthOtpRequestObserver)
//        OneginiComponets.oneginiSDK.setFingerprintAuthenticationObserver(fingerprintAuthenticationObserver)
    }
}