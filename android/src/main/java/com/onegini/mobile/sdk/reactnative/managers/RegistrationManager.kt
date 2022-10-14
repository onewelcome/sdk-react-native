package com.onegini.mobile.sdk.reactnative.managers

import android.net.Uri
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.handlers.customregistration.SimpleCustomRegistrationAction

class RegistrationManager(private val oneginiSDK: OneginiSDK) {

    fun getSimpleCustomRegistrationAction(id: String?): SimpleCustomRegistrationAction? {
        for (action in oneginiSDK.simpleCustomRegistrationActions) {
            if (action.getIdProvider() == id) {
                return action
            }
        }
        return null
    }

    fun handleRegistrationCallback(uri: String?): Boolean {
        return oneginiSDK.registrationRequestHandler.handleRegistrationCallback(Uri.parse(uri))
    }
}
