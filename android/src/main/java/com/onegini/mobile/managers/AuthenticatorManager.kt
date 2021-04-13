package com.onegini.mobile.managers

import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.exception.EmptyOneginiErrorDetails
import com.onegini.mobile.exception.OneginReactNativeException
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticatorDeregistrationHandler
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticatorRegistrationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiError
import com.onegini.mobile.sdk.android.model.OneginiAuthenticator
import com.onegini.mobile.sdk.android.model.entity.UserProfile


class AuthenticatorManager(private val oneginiSDK: OneginiSDK) {


    @Throws(OneginiError::class)
    fun registerFingerprintAuthenticator(profileId: String, handler: OneginiAuthenticatorRegistrationHandler) {
        val userProfile = getUserProfile(profileId)
                ?: throw OneginReactNativeException(OneginReactNativeException.PROFILE_DOES_NOT_EXIST, EmptyOneginiErrorDetails(), "The profileId $profileId does not exist", null)

        val authenticator = getNotRegisteredAuthenticators(userProfile, OneginiAuthenticator.FINGERPRINT)
                ?: throw OneginReactNativeException(OneginReactNativeException.AUTHENTICATOR_DOES_NOT_EXIST, EmptyOneginiErrorDetails(), "The Fingerprint authenticator does not exist", null)

        oneginiSDK.oneginiClient.userClient.registerAuthenticator(authenticator, handler)
    }

    @Throws(OneginiError::class)
    fun deregisterFingerprintAuthenticator(profileId: String, handler: OneginiAuthenticatorDeregistrationHandler) {
        val userProfile = getUserProfile(profileId)
                ?: throw OneginReactNativeException(OneginReactNativeException.PROFILE_DOES_NOT_EXIST, EmptyOneginiErrorDetails(), "The profileId $profileId does not exist", null)

        val authenticator = getRegisteredAuthenticators(userProfile, OneginiAuthenticator.FINGERPRINT)
                ?: throw OneginReactNativeException(OneginReactNativeException.AUTHENTICATOR_DOES_NOT_EXIST, EmptyOneginiErrorDetails(), "The Fingerprint authenticator does not exist", null)

        oneginiSDK.oneginiClient.userClient.deregisterAuthenticator(authenticator, handler)
    }

    @Throws(OneginiError::class)
    fun isFingerprintAuthenticatorRegistered(profileId: String): Boolean {
        val userProfile = getUserProfile(profileId)
                ?: throw OneginReactNativeException(OneginReactNativeException.PROFILE_DOES_NOT_EXIST, EmptyOneginiErrorDetails(), "The profileId $profileId does not exist", null)

        val authenticator = getRegisteredAuthenticators(userProfile, OneginiAuthenticator.FINGERPRINT)

        return authenticator != null
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

    fun getRegisteredAuthenticators(profile: UserProfile, id: String): OneginiAuthenticator? {
        val registeredAuthenticators: Set<OneginiAuthenticator> = oneginiSDK.oneginiClient.userClient.getRegisteredAuthenticators(profile)
        for (auth in registeredAuthenticators) {
            if (auth.id == id) {
                return auth
            }
        }
        return null
    }

    @Throws(OneginiError::class)
    fun getAllAuthenticators(profileId: String): Set<OneginiAuthenticator> {
        val userProfile = getUserProfile(profileId)
                ?: throw OneginReactNativeException(OneginReactNativeException.PROFILE_DOES_NOT_EXIST, EmptyOneginiErrorDetails(), "The profileId $profileId does not exist", null)

        return oneginiSDK.oneginiClient.userClient.getAllAuthenticators(userProfile)
    }

    @Throws(OneginiError::class)
    fun getRegisteredAuthenticators(profileId: String): Set<OneginiAuthenticator> {
        val userProfile = getUserProfile(profileId)
                ?: throw OneginReactNativeException(OneginReactNativeException.PROFILE_DOES_NOT_EXIST, EmptyOneginiErrorDetails(), "The profileId $profileId does not exist", null)

        return oneginiSDK.oneginiClient.userClient.getRegisteredAuthenticators(userProfile)
    }

    @Throws(OneginiError::class)
    fun setPreferredAuthenticator(profileId: String, id: String) {
        val userProfile = getUserProfile(profileId)
                ?: throw OneginReactNativeException(OneginReactNativeException.PROFILE_DOES_NOT_EXIST, EmptyOneginiErrorDetails(), "The profileId $profileId does not exist", null)

        val authenticator = getRegisteredAuthenticators(userProfile, id)
                ?: throw OneginReactNativeException(OneginReactNativeException.AUTHENTICATOR_DOES_NOT_EXIST, EmptyOneginiErrorDetails(), "The $id authenticator does not exist", null)

        oneginiSDK.oneginiClient.userClient.setPreferredAuthenticator(authenticator)
    }
}