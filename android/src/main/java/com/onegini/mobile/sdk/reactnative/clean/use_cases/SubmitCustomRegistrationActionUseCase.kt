package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.ACTION_NOT_ALLOWED
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.IDENTITY_PROVIDER_NOT_FOUND
import com.onegini.mobile.sdk.reactnative.exception.SUBMIT_CUSTOM_REGISTRATION_ACTION_NOT_ALLOWED
import com.onegini.mobile.sdk.reactnative.handlers.customregistration.SimpleCustomRegistrationAction
import com.onegini.mobile.sdk.reactnative.managers.RegistrationManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubmitCustomRegistrationActionUseCase @Inject constructor(private val registrationManager: RegistrationManager) {
    operator fun invoke(identityProviderId: String, token: String?, promise: Promise) {
        when (val action = registrationManager.getSimpleCustomRegistrationAction(identityProviderId)) {
            null -> return promise.reject(IDENTITY_PROVIDER_NOT_FOUND.code.toString(), IDENTITY_PROVIDER_NOT_FOUND.message)
            else -> tryReturnSuccess(action, token, promise)
        }
    }

    private fun tryReturnSuccess(
        action: SimpleCustomRegistrationAction,
        token: String?,
        promise: Promise
    ) {
        try {
            action.returnSuccess(token)
            promise.resolve(null)
        } catch (exception: OneginiReactNativeException) {
            promise.reject(ACTION_NOT_ALLOWED.code.toString(), SUBMIT_CUSTOM_REGISTRATION_ACTION_NOT_ALLOWED)
        }
    }
}
