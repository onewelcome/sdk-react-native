package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.reactnative.facade.UriFacade
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HandleRegistrationCallbackUseCase @Inject constructor(private val oneginiSDK: OneginiSDK, private val uriFacade: UriFacade) {

    operator fun invoke(uriString: String, promise: Promise) {
        val uri = uriFacade.parse(uriString)
        try {
            oneginiSDK.registrationRequestHandler.handleRegistrationCallback(uri)
            promise.resolve(null)
        } catch (exception: OneginiReactNativeException) {
            promise.reject(OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.code, OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.message)
        }
    }
}
