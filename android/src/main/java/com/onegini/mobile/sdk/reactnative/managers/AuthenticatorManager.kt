package com.onegini.mobile.sdk.reactnative.managers

import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticatorDeregistrationHandler
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticatorRegistrationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiAuthenticatorDeregistrationError
import com.onegini.mobile.sdk.android.handlers.error.OneginiAuthenticatorRegistrationError
import com.onegini.mobile.sdk.android.model.OneginiAuthenticator
import com.onegini.mobile.sdk.android.model.entity.CustomInfo
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.AUTHENTICATOR_DOES_NOT_EXIST
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.NO_PROFILE_AUTHENTICATED
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.PROFILE_DOES_NOT_EXIST
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthenticatorManager @Inject constructor(private val oneginiSDK: OneginiSDK) {

    fun registerFingerprintAuthenticator(profileId: String, callback: RegistrationCallback) {
        val userProfile = getUserProfile(profileId)
        if (userProfile == null) {
            callback.onError(PROFILE_DOES_NOT_EXIST.code.toString(), PROFILE_DOES_NOT_EXIST.message)
            return
        }

        val authenticator = getNotRegisteredAuthenticators(userProfile, OneginiAuthenticator.FINGERPRINT)
        if (authenticator == null) {
            callback.onError(AUTHENTICATOR_DOES_NOT_EXIST.code.toString(), AUTHENTICATOR_DOES_NOT_EXIST.message)
            return
        }

        oneginiSDK.oneginiClient.userClient.registerAuthenticator(
            authenticator,
            object : OneginiAuthenticatorRegistrationHandler {
                override fun onSuccess(info: CustomInfo?) {
                    callback.onSuccess(info)
                }

                override fun onError(error: OneginiAuthenticatorRegistrationError) {
                    callback.onError(error.errorType.toString(), error.message)
                }
            }
        )
    }

    fun deregisterFingerprintAuthenticator(profileId: String, callback: DeregistrationCallback) {
        val userProfile = getUserProfile(profileId)
        if (userProfile == null) {
            callback.onError(PROFILE_DOES_NOT_EXIST.code.toString(), PROFILE_DOES_NOT_EXIST.message)
            return
        }

        val authenticator = getRegisteredAuthenticators(userProfile, OneginiAuthenticator.FINGERPRINT)
        if (authenticator == null) {
            callback.onError(AUTHENTICATOR_DOES_NOT_EXIST.code.toString(), AUTHENTICATOR_DOES_NOT_EXIST.message)
            return
        }

        oneginiSDK.oneginiClient.userClient.deregisterAuthenticator(
            authenticator,
            object : OneginiAuthenticatorDeregistrationHandler {
                override fun onSuccess() {
                    callback.onSuccess()
                }

                override fun onError(error: OneginiAuthenticatorDeregistrationError) {
                    callback.onError(error.errorType.toString(), error.message)
                }
            })
    }

    fun getUserProfile(profileId: String?): UserProfile? {
        if (profileId == null) {
            return null
        }
        oneginiSDK.oneginiClient.userClient.userProfiles.forEach {
            if (it.profileId == profileId) {
                return it
            }
        }
        return null
    }

    fun getNotRegisteredAuthenticators(profile: UserProfile, type: Int): OneginiAuthenticator? {
        val notRegisteredAuthenticators: Set<OneginiAuthenticator> = oneginiSDK.oneginiClient.userClient.getNotRegisteredAuthenticators(profile)
        for (auth in notRegisteredAuthenticators) {
            if (auth.type == type) {
                return auth
            }
        }
        return null
    }

    fun getRegisteredAuthenticators(profile: UserProfile, type: Int): OneginiAuthenticator? {
        val registeredAuthenticators: Set<OneginiAuthenticator> = oneginiSDK.oneginiClient.userClient.getRegisteredAuthenticators(profile)
        for (auth in registeredAuthenticators) {
            if (auth.type == type) {
                return auth
            }
        }
        return null
    }

    fun getRegisteredAuthenticator(profile: UserProfile, id: String): OneginiAuthenticator? {
        return oneginiSDK.oneginiClient.userClient
            .getRegisteredAuthenticators(profile)
            .firstOrNull { it.id == id }
    }

    fun getAuthenticator(profile: UserProfile, id: String): OneginiAuthenticator? {
        return oneginiSDK.oneginiClient.userClient
            .getAllAuthenticators(profile)
            .firstOrNull { it.id == id }
    }

    fun setPreferredAuthenticator(authenticatorId: String) {
        val userProfile = oneginiSDK.oneginiClient.userClient.authenticatedUserProfile
            ?: throw OneginiReactNativeException(NO_PROFILE_AUTHENTICATED)

        val authenticator = getRegisteredAuthenticator(userProfile, authenticatorId)
            ?: throw OneginiReactNativeException(AUTHENTICATOR_DOES_NOT_EXIST)

        oneginiSDK.oneginiClient.userClient.setPreferredAuthenticator(authenticator)
    }

    interface RegistrationCallback {
        fun onSuccess(customInfo: CustomInfo?)
        fun onError(code: String?, message: String?)
    }

    interface DeregistrationCallback {
        fun onSuccess()
        fun onError(code: String?, message: String?)
    }
}
