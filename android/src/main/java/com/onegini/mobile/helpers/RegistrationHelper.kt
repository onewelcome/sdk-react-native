/*
 * Copyright (c) 2016-2018 Onegini B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.onegini.mobile.helpers

import android.net.Uri
import com.onegini.mobile.Constants.DEFAULT_SCOPES
import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.sdk.android.handlers.OneginiRegistrationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiAuthenticationError
import com.onegini.mobile.sdk.android.handlers.error.OneginiRegistrationError
import com.onegini.mobile.sdk.android.handlers.error.OneginiRegistrationError.RegistrationErrorType
import com.onegini.mobile.sdk.android.model.OneginiIdentityProvider
import com.onegini.mobile.view.handlers.customregistration.SimpleCustomRegistrationAction

class RegistrationHelper(private val oneginiSDK: OneginiSDK) {
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

    fun getErrorMessageByCode(@RegistrationErrorType errorType: Int): String? {
        val errorMessage: String?
        errorMessage = when (errorType) {
            OneginiRegistrationError.DEVICE_DEREGISTERED -> "The device was deregistered, please try registering again"
            OneginiRegistrationError.ACTION_CANCELED -> "Registration was cancelled"
            OneginiAuthenticationError.NETWORK_CONNECTIVITY_PROBLEM -> "No internet connection."
            OneginiAuthenticationError.SERVER_NOT_REACHABLE -> "The server is not reachable."
            OneginiRegistrationError.OUTDATED_APP -> "Please update this application in order to use it."
            OneginiRegistrationError.OUTDATED_OS -> "Please update your Android version to use this application."
            OneginiRegistrationError.INVALID_IDENTITY_PROVIDER -> "The Identity provider you were trying to use is invalid."
            OneginiRegistrationError.CUSTOM_REGISTRATION_EXPIRED -> "Custom registration request has expired. Please retry."
            OneginiRegistrationError.CUSTOM_REGISTRATION_FAILURE -> "Custom registration request has failed, see logcat for more details."
            OneginiRegistrationError.GENERAL_ERROR -> "General error"
            else -> null
        }
        return errorMessage
    }

    fun handleRegistrationCallback(uri: String?) {
        oneginiSDK.registrationRequestHandler.handleRegistrationCallback(Uri.parse(uri))
    }

    fun cancelRegistration() {
        oneginiSDK.registrationRequestHandler.onRegistrationCanceled()
    }
}