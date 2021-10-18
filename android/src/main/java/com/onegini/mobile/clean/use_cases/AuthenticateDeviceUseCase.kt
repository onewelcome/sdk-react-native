package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableArray
import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.mapers.RegistrationScopesMapper
import com.onegini.mobile.sdk.android.handlers.OneginiDeviceAuthenticationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiDeviceAuthenticationError

class AuthenticateDeviceUseCase(private val oneginiSDK: OneginiSDK) {

    operator fun invoke(scopes: ReadableArray, promise: Promise) {
        val scopesArray = RegistrationScopesMapper.toStringArray(scopes)

        oneginiSDK.oneginiClient.deviceClient.authenticateDevice(
            scopesArray,
            object : OneginiDeviceAuthenticationHandler {
                override fun onSuccess() {
                    promise.resolve(null)
                }

                override fun onError(error: OneginiDeviceAuthenticationError) {
                    promise.reject(error.errorType.toString(), error.message)
                }
            }
        )
    }
}
