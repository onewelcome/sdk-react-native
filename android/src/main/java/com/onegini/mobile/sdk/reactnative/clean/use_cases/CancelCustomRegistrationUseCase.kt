package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.exception.CANCEL_CUSTOM_REGISTRATION_NOT_ALLOWED
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.reactnative.handlers.customregistration.CustomRegistrationAction
import com.onegini.mobile.sdk.reactnative.managers.RegistrationActionManager
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CancelCustomRegistrationUseCase @Inject constructor(private val registrationActionManager: RegistrationActionManager) {
    operator fun invoke(message: String, promise: Promise) {
        registrationActionManager.getActiveCustomRegistrationAction()?.let { action ->
            tryCancelCustomRegistrationAction(action, message, promise)
        } ?: promise.reject(OneginiWrapperErrors.ACTION_NOT_ALLOWED.code.toString(), CANCEL_CUSTOM_REGISTRATION_NOT_ALLOWED)
    }

    private fun tryCancelCustomRegistrationAction(
        action: CustomRegistrationAction,
        message: String,
        promise: Promise
    ) {
        try {
            action.returnError(Exception(message))
            promise.resolve(null)
        } catch (exception: OneginiReactNativeException) {
            promise.reject(OneginiWrapperErrors.ACTION_NOT_ALLOWED.code.toString(), CANCEL_CUSTOM_REGISTRATION_NOT_ALLOWED)
        }
    }
}
