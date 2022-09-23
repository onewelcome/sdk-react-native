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
import com.onegini.mobile.sdk.reactnative.clean.use_cases.ValidatePinWithPolicyUseCase

class OneginiSdkWrapper(
    private val oneginiSDK: OneginiSDK,
    private val reactApplicationContext: ReactApplicationContext,
    val startClientUseCase: StartClientUseCase = StartClientUseCase(oneginiSDK, reactApplicationContext),
    val getIdentityProvidersUseCase: GetIdentityProvidersUseCase = GetIdentityProvidersUseCase(oneginiSDK),
    val getAccessTokenUseCase: GetAccessTokenUseCase = GetAccessTokenUseCase(oneginiSDK),
    val registerUserUseCase: RegisterUserUseCase = RegisterUserUseCase(oneginiSDK),
    val getAuthenticatedUserProfileUseCase: GetAuthenticatedUserProfileUseCase = GetAuthenticatedUserProfileUseCase(oneginiSDK),
    val getAllAuthenticatorsUseCase: GetAllAuthenticatorsUseCase = GetAllAuthenticatorsUseCase(oneginiSDK),
    val getRegisteredAuthenticatorsUseCase: GetRegisteredAuthenticatorsUseCase = GetRegisteredAuthenticatorsUseCase(oneginiSDK),
    val validatePinWithPolicyUseCase: ValidatePinWithPolicyUseCase = ValidatePinWithPolicyUseCase(oneginiSDK),
    val getUserProfilesUseCase: GetUserProfilesUseCase = GetUserProfilesUseCase(oneginiSDK),
    val getRedirectUriUseCase: GetRedirectUriUseCase = GetRedirectUriUseCase(oneginiSDK),
    val deregisterUserUseCase: DeregisterUserUseCase = DeregisterUserUseCase(oneginiSDK),
    val authenticateUserUseCase: AuthenticateUserUseCase = AuthenticateUserUseCase(oneginiSDK)
)  {

    fun startClient(rnConfig: ReadableMap, promise: Promise) {
        startClientUseCase(rnConfig, promise)
    }

    fun authenticateUser(profileId: String, authenticatorId: String, promise: Promise) {
        authenticateUserUseCase(profileId, authenticatorId, promise)
    }

    fun authenticateUserImplicitly(profileId: String, scopes: ReadableArray, promise: Promise) {
        TODO("Not yet implemented")
    }

    fun authenticateDeviceForResource(scopes: ReadableArray, promise: Promise) {
        TODO("Not yet implemented")
    }

    fun getUserProfiles(promise: Promise) {
        getUserProfilesUseCase(promise)
    }

    fun logout(promise: Promise) {
        TODO("Not yet implemented")
    }

    fun getAccessToken(promise: Promise) {
        getAccessTokenUseCase(promise)
    }

    fun getAuthenticatedUserProfile(promise: Promise) {
        getAuthenticatedUserProfileUseCase(promise)
    }

    fun getAllAuthenticators(profileId: String, promise: Promise) {
        getAllAuthenticatorsUseCase(profileId, promise)
    }

    fun getRegisteredAuthenticators(profileId: String, promise: Promise) {
        getRegisteredAuthenticatorsUseCase(profileId, promise)
    }

    fun setPreferredAuthenticator(profileId: String, idOneginiAuthenticator: String, promise: Promise) {
        TODO("Not yet implemented")
    }

    fun validatePinWithPolicy(pin: String?, promise: Promise) {
        validatePinWithPolicyUseCase(pin, promise)
    }

    fun registerUser(identityProviderId: String?, scopes: ReadableArray?, promise: Promise) {
        registerUserUseCase(identityProviderId, scopes, promise)
    }

    fun deregisterUser(profileId: String, promise: Promise) {
        deregisterUserUseCase(profileId, promise)
    }

    fun cancelRegistration() {
        TODO("Not yet implemented")
    }

    fun getRedirectUri(promise: Promise) {
        getRedirectUriUseCase(promise)
    }

    fun handleRegistrationCallback(uri: String) {
        TODO("Not yet implemented")
    }

    fun submitCustomRegistrationAction(customAction: String, identityProviderId: String, token: String) {
        TODO("Not yet implemented")
    }

    fun getIdentityProviders(promise: Promise) {
        getIdentityProvidersUseCase(promise)
    }

    fun registerFingerprintAuthenticator(profileId: String, promise: Promise) {
        TODO("Not yet implemented")
    }

    fun isFingerprintAuthenticatorRegistered(profileId: String, promise: Promise) {
        TODO("Not yet implemented")
    }

    fun deregisterFingerprintAuthenticator(profileId: String, promise: Promise) {
        TODO("Not yet implemented")
    }

    fun submitFingerprintAcceptAuthenticationRequest(promise: Promise) {
        TODO("Not yet implemented")
    }

    fun submitFingerprintDenyAuthenticationRequest(promise: Promise) {
        TODO("Not yet implemented")
    }

    fun submitFingerprintFallbackToPin(promise: Promise) {
        TODO("Not yet implemented")
    }

    fun changePin(promise: Promise) {
        TODO("Not yet implemented")
    }

    fun submitPinAction(flowString: String, action: String, pin: String) {
        TODO("Not yet implemented")
    }

    fun enrollMobileAuthentication(promise: Promise) {
        TODO("Not yet implemented")
    }

    fun acceptMobileAuthConfirmation(promise: Promise) {
        TODO("Not yet implemented")
    }

    fun denyMobileAuthConfirmation(promise: Promise) {
        TODO("Not yet implemented")
    }

    fun handleMobileAuthWithOtp(otpCode: String, promise: Promise) {
        TODO("Not yet implemented")
    }

    fun resourceRequest(type: String, details: ReadableMap, promise: Promise) {
        TODO("Not yet implemented")
    }

    fun startSingleSignOn(url: String, promise: Promise) {
        TODO("Not yet implemented")
    }
}
