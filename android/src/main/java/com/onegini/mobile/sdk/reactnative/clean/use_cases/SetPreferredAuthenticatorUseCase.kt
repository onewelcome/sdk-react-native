package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.managers.AuthenticatorManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetPreferredAuthenticatorUseCase @Inject constructor(private val authenticatorManager: AuthenticatorManager) {
    operator fun invoke(authenticatorId: String, promise: Promise) {
        try {
            authenticatorManager.setPreferredAuthenticator(authenticatorId)
            promise.resolve(null)
        } catch (e: OneginiReactNativeException) {
            promise.reject(e.errorType.toString(), e.message)
        }
    }
}
