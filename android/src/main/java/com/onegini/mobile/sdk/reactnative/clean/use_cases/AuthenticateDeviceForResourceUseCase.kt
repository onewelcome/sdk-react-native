package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableArray
import com.onegini.mobile.sdk.android.handlers.OneginiDeviceAuthenticationHandler
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.android.handlers.error.OneginiDeviceAuthenticationError
import com.onegini.mobile.sdk.reactnative.mapers.ScopesMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticateDeviceForResourceUseCase @Inject constructor(private val oneginiSDK: OneginiSDK) {
    operator fun invoke(scopes: ReadableArray?, promise: Promise) {
        val scopesArray = ScopesMapper.toStringArray(scopes)
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
