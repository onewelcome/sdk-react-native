package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.managers.AuthenticatorManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SetPreferredAuthenticatorUseCase @Inject constructor(private val authenticatorManager: AuthenticatorManager) {
    operator fun invoke(profileId: String, idOneginiAuthenticator: String, promise: Promise) {
        try {
            authenticatorManager.setPreferredAuthenticator(profileId, idOneginiAuthenticator)
            promise.resolve(null)
        } catch (e: OneginiReactNativeException) {
            promise.reject(e.errorType.toString(), e.message)
        }
    }
}
