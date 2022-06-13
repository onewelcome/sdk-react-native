package com.onegini.mobile.sdk.reactnative.managers

import android.net.Uri
import com.onegini.mobile.sdk.android.handlers.OneginiRegistrationHandler
import com.onegini.mobile.sdk.android.model.OneginiIdentityProvider
import com.onegini.mobile.sdk.reactnative.Constants.DEFAULT_SCOPES
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.handlers.customregistration.SimpleCustomRegistrationAction

class RegistrationManager(private val oneginiSDK: OneginiSDK) {
    val redirectUri: String
        get() {
            val client = oneginiSDK.oneginiClient
            return client.configModel.redirectUri
        }

    fun registerUser(identityProvider: OneginiIdentityProvider?, registrationHandler: OneginiRegistrationHandler?) {
        val oneginiClient = oneginiSDK.oneginiClient
        oneginiClient.userClient.registerUser(identityProvider, DEFAULT_SCOPES, registrationHandler!!)
    }

    fun registerUser(id: String?, registrationHandler: OneginiRegistrationHandler?) {
        registerUser(getIdentityProvider(id), registrationHandler)
    }

    fun getIdentityProvider(id: String?): OneginiIdentityProvider? {
        for (identity in oneginiSDK.oneginiClient.userClient.identityProviders) {
            if (identity.id == id) {
                return identity
            }
        }
        return null
    }

    fun getSimpleCustomRegistrationAction(id: String?): SimpleCustomRegistrationAction? {
        for (action in oneginiSDK.simpleCustomRegistrationActions) {
            if (action.getIdProvider() == id) {
                return action
            }
        }
        return null
    }

    fun handleRegistrationCallback(uri: String?) {
        oneginiSDK.registrationRequestHandler.handleRegistrationCallback(Uri.parse(uri))
    }

    fun cancelRegistration() {
        oneginiSDK.registrationRequestHandler.onRegistrationCanceled()
    }
}
