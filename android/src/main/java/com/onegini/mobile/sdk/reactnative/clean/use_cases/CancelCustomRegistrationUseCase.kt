package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.CANCEL_CUSTOM_REGISTRATION_NOT_ALLOWED
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.reactnative.handlers.customregistration.SimpleCustomRegistrationAction
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CancelCustomRegistrationUseCase @Inject constructor(private val oneginiSdk: OneginiSDK) {
    operator fun invoke(message: String, promise: Promise) {
        getActiveCustomRegistrationAction()?.let { action ->
            try {
                action.returnError(Exception(message))
                return promise.resolve(null)
            } catch (exception: OneginiReactNativeException) {
                promise.reject(OneginiWrapperErrors.ACTION_NOT_ALLOWED.code.toString(), CANCEL_CUSTOM_REGISTRATION_NOT_ALLOWED)
            }
        } ?: promise.reject(OneginiWrapperErrors.ACTION_NOT_ALLOWED.code.toString(), CANCEL_CUSTOM_REGISTRATION_NOT_ALLOWED)
    }

    private fun getActiveCustomRegistrationAction(): SimpleCustomRegistrationAction? {
        for (action in oneginiSdk.simpleCustomRegistrationActions) {
            if (action.isInProgress()){
                return action
            }
        }
        return null
    }
}
