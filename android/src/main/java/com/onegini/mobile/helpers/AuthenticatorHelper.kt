package com.onegini.mobile.helpers

import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.sdk.android.model.OneginiAuthenticator
import com.onegini.mobile.sdk.android.model.entity.UserProfile


class AuthenticatorHelper(private val oneginiSDK: OneginiSDK) {

    fun registerFingerprintAuthenticator(profileId: String) {
        val userProfile = getUserProfile(profileId)

    }

    fun getUserProfile(profileId: String): UserProfile? {
        oneginiSDK.oneginiClient.userClient.userProfiles.forEach {
            if (it.profileId == profileId) {
                return it
            }
        }
        return null
    }

    fun getNotRegisteredAuthenticators(): OneginiAuthenticator? {
        val notRegisteredAuthenticators: Set<OneginiAuthenticator> = oneginiSDK.oneginiClient.getUserClient().getNotRegisteredAuthenticators(authenticatedUserProfile)
        for (auth in notRegisteredAuthenticators) {
            if (auth.type == OneginiAuthenticator.FINGERPRINT) {
                // the fingerprint authenticator is available for registration
                fingerprintAuthenticator = auth
            }
        }
    }
}