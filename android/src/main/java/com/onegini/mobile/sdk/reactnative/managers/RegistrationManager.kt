package com.onegini.mobile.sdk.reactnative.managers

import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.handlers.customregistration.SimpleCustomRegistrationAction
import com.onegini.mobile.sdk.reactnative.handlers.registration.RegistrationRequestHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegistrationManager @Inject constructor(
    private val oneginiSDK: OneginiSDK,
    private val registrationRequestHandler: RegistrationRequestHandler
) {

    fun getSimpleCustomRegistrationAction(id: String?): SimpleCustomRegistrationAction? {
        for (action in oneginiSDK.simpleCustomRegistrationActions) {
            if (action.getIdProvider() == id) {
                return action
            }
        }
        return null
    }
}
