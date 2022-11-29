package com.onegini.mobile.sdk.reactnative.clean.use_cases

import android.net.Uri
import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors

class HandleRegistrationCallbackUseCase(private val oneginiSDK: OneginiSDK) {

    operator fun invoke(uriString: String?, promise: Promise) {
        val uri = Uri.parse(uriString)
        try {
            oneginiSDK.registrationRequestHandler.handleRegistrationCallback(uri)
            promise.resolve(null)
        } catch (exception: OneginiReactNativeException) {
            promise.reject(OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.code, OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.message)
        }
    }
}
