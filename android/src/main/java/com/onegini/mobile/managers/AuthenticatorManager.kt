package com.onegini.mobile.managers

import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticatorDeregistrationHandler
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticatorRegistrationHandler
import com.onegini.mobile.sdk.android.model.OneginiAuthenticator
import com.onegini.mobile.sdk.android.model.entity.UserProfile


class AuthenticatorManager(private val oneginiSDK: OneginiSDK) {

    @Throws(Exception::class)
    fun registerFingerprintAuthenticator(profileId: String, handler: OneginiAuthenticatorRegistrationHandler) {
        val userProfile = getUserProfile(profileId)
                ?: throw Exception("The profileId $profileId does not exist")

        val authenticator = getNotRegisteredAuthenticators(userProfile, OneginiAuthenticator.FINGERPRINT)
                ?: throw Exception("The Fingerprint authenticator does not exist")

        oneginiSDK.oneginiClient.userClient.registerAuthenticator(authenticator, handler)
    }

    @Throws(Exception::class)
    fun deregisterFingerprintAuthenticator(profileId: String, handler: OneginiAuthenticatorDeregistrationHandler) {
        val userProfile = getUserProfile(profileId)
                ?: throw Exception("The profileId $profileId does not exist")

        val authenticator = getRegisteredAuthenticators(userProfile, OneginiAuthenticator.FINGERPRINT)
                ?: throw Exception("The Fingerprint authenticator does not exist")

        oneginiSDK.oneginiClient.userClient.deregisterAuthenticator(authenticator, handler)
    }

    @Throws(Exception::class)
    fun isFingerprintAuthenticatorRegistered(profileId: String): Boolean {
        val userProfile = getUserProfile(profileId)
                ?: throw Exception("The profileId $profileId does not exist")

        val authenticator = getRegisteredAuthenticators(userProfile, OneginiAuthenticator.FINGERPRINT)

        return authenticator != null
    }

    fun getUserProfile(profileId: String): UserProfile? {
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
        val notRegisteredAuthenticators: Set<OneginiAuthenticator> = oneginiSDK.oneginiClient.userClient.getRegisteredAuthenticators(profile)
        for (auth in notRegisteredAuthenticators) {
            if (auth.type == type) {
                return auth
            }
        }
        return null
    }
}