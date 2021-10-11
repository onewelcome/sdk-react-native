package com.onegini.mobile.clean.wrapper

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableArray
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

    fun authenticateUser(profileId: String?, authenticatorId: String?, promise: Promise)

    fun authenticateUserImplicitly(profileId: String?, scopes: ReadableArray, promise: Promise)

    fun authenticateDeviceForResource(resourcePath: String, promise: Promise)

    fun getUserProfiles(promise: Promise)

    fun logout(promise: Promise)

    fun getAccessToken(promise: Promise)

    fun getAuthenticatedUserProfile(promise: Promise)

    fun getAllAuthenticators(profileId: String, promise: Promise)

    fun getRegisteredAuthenticators(profileId: String, promise: Promise)

    fun setPreferredAuthenticator(profileId: String, idOneginiAuthenticator: String, promise: Promise)

    //
    // Registration
    //

    fun registerUser(identityProviderId: String?, scopes: ReadableArray, promise: Promise)

    fun deregisterUser(profileId: String?, promise: Promise)

    fun cancelRegistration()

    fun getRedirectUri(promise: Promise)

    fun handleRegistrationCallback(uri: String?)

    fun submitCustomRegistrationAction(customAction: String, identityProviderId: String, token: String?)

    fun getIdentityProviders(promise: Promise)

    //
    // Fingerprint
    //

    fun registerFingerprintAuthenticator(profileId: String, promise: Promise)

    fun isFingerprintAuthenticatorRegistered(profileId: String, promise: Promise)

    fun deregisterFingerprintAuthenticator(profileId: String, promise: Promise)

    fun submitFingerprintAcceptAuthenticationRequest(promise: Promise)

    fun submitFingerprintDenyAuthenticationRequest(promise: Promise)

    fun submitFingerprintFallbackToPin(promise: Promise)

    //
    // PIN
    //

    fun changePin(promise: Promise)

    fun submitPinAction(flowString: String?, action: String, pin: String?)

    //
    // Mobile
    //

    fun enrollMobileAuthentication(promise: Promise)

    fun acceptMobileAuthConfirmation(promise: Promise)

    fun denyMobileAuthConfirmation(promise: Promise)

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
