package com.onegini.mobile.sdk.reactnative.clean.wrapper

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.onegini.mobile.sdk.reactnative.clean.use_cases.AuthenticateUserUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.DeregisterUserUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetAllAuthenticatorsUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetRedirectUriUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetRegisteredAuthenticatorsUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetUserProfilesUseCase
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetAccessTokenUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetAuthenticatedUserProfileUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetIdentityProvidersUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.RegisterUserUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.StartClientUseCase

class OneginiSdkWrapper(
    private val oneginiSDK: OneginiSDK,
    val startClientUseCase: StartClientUseCase = StartClientUseCase(oneginiSDK),
    val getIdentityProvidersUseCase: GetIdentityProvidersUseCase = GetIdentityProvidersUseCase(oneginiSDK),
    val getAccessTokenUseCase: GetAccessTokenUseCase = GetAccessTokenUseCase(oneginiSDK),
    val registerUserUseCase: RegisterUserUseCase = RegisterUserUseCase(oneginiSDK),
    val getAuthenticatedUserProfileUseCase: GetAuthenticatedUserProfileUseCase = GetAuthenticatedUserProfileUseCase(oneginiSDK),
    val getAllAuthenticatorsUseCase: GetAllAuthenticatorsUseCase = GetAllAuthenticatorsUseCase(oneginiSDK),
    val getRegisteredAuthenticatorsUseCase: GetRegisteredAuthenticatorsUseCase = GetRegisteredAuthenticatorsUseCase(oneginiSDK),
    val getUserProfilesUseCase: GetUserProfilesUseCase = GetUserProfilesUseCase(oneginiSDK),
    val getRedirectUriUseCase: GetRedirectUriUseCase = GetRedirectUriUseCase(oneginiSDK),
    val deregisterUserUseCase: DeregisterUserUseCase = DeregisterUserUseCase(oneginiSDK),
    val authenticateUserUseCase: AuthenticateUserUseCase = AuthenticateUserUseCase(oneginiSDK)
) : IOneginiSdkWrapper {

    override fun startClient(rnConfig: ReadableMap, promise: Promise) {
        startClientUseCase(rnConfig, promise)
    }

    override fun authenticateUser(profileId: String?, authenticatorId: String?, promise: Promise) {
        authenticateUserUseCase(profileId, authenticatorId, promise)
    }

    override fun authenticateUserImplicitly(profileId: String?, scopes: ReadableArray, promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun authenticateDeviceForResource(scopes: ReadableArray, promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun getUserProfiles(promise: Promise) {
        getUserProfilesUseCase(promise)
    }

    override fun logout(promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun getAccessToken(promise: Promise) {
        getAccessTokenUseCase(promise)
    }

    override fun getAuthenticatedUserProfile(promise: Promise) {
        getAuthenticatedUserProfileUseCase(promise)
    }

    override fun getAllAuthenticators(profileId: String, promise: Promise) {
        getAllAuthenticatorsUseCase(profileId, promise)
    }

    override fun getRegisteredAuthenticators(profileId: String, promise: Promise) {
        getRegisteredAuthenticatorsUseCase(profileId, promise)
    }

    override fun setPreferredAuthenticator(profileId: String, idOneginiAuthenticator: String, promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun registerUser(identityProviderId: String?, scopes: ReadableArray, promise: Promise) {
        registerUserUseCase(identityProviderId, scopes, promise)
    }

    override fun deregisterUser(profileId: String?, promise: Promise) {
        deregisterUserUseCase(profileId, promise)
    }

    override fun cancelRegistration(promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun getRedirectUri(promise: Promise) {
        getRedirectUriUseCase(promise)
    }

    override fun handleRegistrationCallback(uri: String?, promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun submitCustomRegistrationAction(customAction: String, identityProviderId: String, token: String?, promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun getIdentityProviders(promise: Promise) {
        getIdentityProvidersUseCase(promise)
    }

    override fun registerFingerprintAuthenticator(profileId: String, promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun isFingerprintAuthenticatorRegistered(profileId: String, promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun deregisterFingerprintAuthenticator(profileId: String, promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun submitFingerprintAcceptAuthenticationRequest(promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun submitFingerprintDenyAuthenticationRequest(promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun submitFingerprintFallbackToPin(promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun changePin(promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun submitPinAction(pinFlow: String?, action: String?, pin: String?, promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun enrollMobileAuthentication(promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun acceptMobileAuthConfirmation(promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun denyMobileAuthConfirmation(promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun handleMobileAuthWithOtp(otpCode: String, promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun resourceRequest(type: String, details: ReadableMap, promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun startSingleSignOn(url: String, promise: Promise) {
        TODO("Not yet implemented")
    }
}
