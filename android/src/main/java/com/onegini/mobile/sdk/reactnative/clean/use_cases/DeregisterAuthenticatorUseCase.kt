package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticatorDeregistrationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiAuthenticatorDeregistrationError
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.AUTHENTICATOR_DOES_NOT_EXIST
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.AUTHENTICATOR_NOT_REGISTERED
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.NO_PROFILE_AUTHENTICATED
import com.onegini.mobile.sdk.reactnative.managers.AuthenticatorManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeregisterAuthenticatorUseCase @Inject constructor(
    private val oneginiSDK: OneginiSDK,
    private val authenticatorManager: AuthenticatorManager
) {
    operator fun invoke(idOneginiAuthenticator: String, promise: Promise) {
        val userProfile = oneginiSDK.oneginiClient.userClient.authenticatedUserProfile ?:
            return promise.reject(NO_PROFILE_AUTHENTICATED.code.toString(), NO_PROFILE_AUTHENTICATED.message)

        val authenticator = authenticatorManager.getAuthenticator(userProfile, idOneginiAuthenticator) ?:
            return promise.reject(AUTHENTICATOR_DOES_NOT_EXIST.code.toString(), AUTHENTICATOR_DOES_NOT_EXIST.message)

        if (!authenticator.isRegistered) {
            return promise.reject(AUTHENTICATOR_NOT_REGISTERED.code.toString(), AUTHENTICATOR_NOT_REGISTERED.message)
        }
        oneginiSDK.oneginiClient.userClient.deregisterAuthenticator(
            authenticator,
            object : OneginiAuthenticatorDeregistrationHandler {
                override fun onSuccess() {
                    promise.resolve(null)
                }

                override fun onError(error: OneginiAuthenticatorDeregistrationError) {
                    promise.reject(error.errorType.toString(), error.message)
                }
            })
    }
}
