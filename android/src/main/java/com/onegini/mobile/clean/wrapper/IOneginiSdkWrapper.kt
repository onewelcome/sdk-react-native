package com.onegini.mobile.clean.wrapper

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import com.onegini.mobile.mapers.*
import com.onegini.mobile.sdk.android.handlers.*
import com.onegini.mobile.sdk.android.handlers.error.*

interface IOneginiSdkWrapper {

    //
    // Setup
    //

    fun startClient(rnConfig: ReadableMap, promise: Promise)

    //
    // Authentication
    //

    fun authenticateUser(profileId: String?, promise: Promise)

    fun authenticateUserImplicitly(profileId: String?, promise: Promise)

    fun authenticateDeviceForResource(resourcePath: String, promise: Promise)

    fun getUserProfiles(promise: Promise)

    fun logout(promise: Promise)

    fun getAccessToken(promise: Promise)

    fun getAuthenticatedUserProfile(promise: Promise)

    //
    // Registration
    //

    fun registerUser(identityProviderId: String?, promise: Promise)

    fun deregisterUser(profileId: String?, promise: Promise)

    fun cancelRegistration()

    fun getRedirectUri(promise: Promise)

    fun handleRegistrationCallback(uri: String?)

    fun submitCustomRegistrationAction(customAction: String, identityProviderId: String, token: String?)

    fun getIdentityProviders(promise: Promise)

    //
    // Authenticators
    //

    fun registerAuthenticator(profileId: String, type: String, promise: Promise)

    fun isAuthenticatorRegistered(profileId: String, type: String, promise: Promise)

    fun deregisterAuthenticator(profileId: String, type: String, promise: Promise)

    fun submitFingerprintFallbackToPin()

    fun getAllAuthenticators(profileId: String, promise: Promise)

    fun getRegisteredAuthenticators(profileId: String, promise: Promise)

    fun setPreferredAuthenticator(profileId: String, idOneginiAuthenticator: String, promise: Promise)

    fun acceptAuthenticationRequest(type: String, value: String?)

    fun denyAuthenticationRequest(type: String)

    //
    // PIN
    //

    fun changePin(promise: Promise)

    fun submitPinAction(flowString: String?, action: String, pin: String?)

    //
    // Mobile
    //

    fun enrollMobileAuthentication(promise: Promise)

    fun handleMobileAuthWithOtp(otpCode: String, promise: Promise)

    //
    // Resources
    //

    fun resourceRequest(type: String, details: ReadableMap, promise: Promise)

    //
    //
    //

    fun startSingleSignOn(url: String, promise: Promise)
}
