package com.onegini.mobile.sdk.reactnative.managers

import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.handlers.customregistration.SimpleCustomRegistrationAction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegistrationManager @Inject constructor(private val oneginiSDK: OneginiSDK) {

    fun getSimpleCustomRegistrationAction(id: String?): SimpleCustomRegistrationAction? {
        for (action in oneginiSDK.simpleCustomRegistrationActions) {
            if (action.id == id) {
                return action
            }
        }
        return null
    }
}
