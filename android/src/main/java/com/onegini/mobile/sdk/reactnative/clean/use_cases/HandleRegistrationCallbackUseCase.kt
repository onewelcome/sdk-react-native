package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS
import com.onegini.mobile.sdk.reactnative.facade.UriFacade
import com.onegini.mobile.sdk.reactnative.handlers.registration.RegistrationRequestHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HandleRegistrationCallbackUseCase @Inject constructor(
    private val registrationRequestHandler: RegistrationRequestHandler,
    private val uriFacade: UriFacade
) {

    operator fun invoke(uriString: String, promise: Promise) {
        val uri = uriFacade.parse(uriString)
        try {
            registrationRequestHandler.handleRegistrationCallback(uri)
            promise.resolve(null)
        } catch (exception: OneginiReactNativeException) {
            promise.reject(REGISTRATION_NOT_IN_PROGRESS.code.toString(), REGISTRATION_NOT_IN_PROGRESS.message)
        }
    }
}
