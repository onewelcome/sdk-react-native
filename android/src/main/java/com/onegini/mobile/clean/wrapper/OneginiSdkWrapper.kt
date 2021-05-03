package com.onegini.mobile.clean.wrapper

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.clean.use_cases.*

class OneginiSdkWrapper(
    private val oneginiSDK: OneginiSDK,
    private val reactApplicationContext: ReactApplicationContext,
    val startClientUseCase: StartClientUseCase = StartClientUseCase(oneginiSDK, reactApplicationContext),
    val getIdentityProvidersUseCase: GetIdentityProvidersUseCase = GetIdentityProvidersUseCase(),
    val getAccessTokenUseCase: GetAccessTokenUseCase = GetAccessTokenUseCase(),
    val registerUserUseCase: RegisterUserUseCase = RegisterUserUseCase(),
    val getAuthenticatedUserProfileUseCase: GetAuthenticatedUserProfileUseCase = GetAuthenticatedUserProfileUseCase(),
    val getAllAuthenticatorsUseCase: GetAllAuthenticatorsUseCase = GetAllAuthenticatorsUseCase(),
    val getRegisteredAuthenticatorsUseCase: GetRegisteredAuthenticatorsUseCase = GetRegisteredAuthenticatorsUseCase(),
    val getUserProfilesUseCase: GetUserProfilesUseCase = GetUserProfilesUseCase(),
    val getRedirectUriUseCase: GetRedirectUriUseCase = GetRedirectUriUseCase()
) : IOneginiSdkWrapper {

    override fun startClient(rnConfig: ReadableMap, promise: Promise) {
        startClientUseCase(rnConfig, promise)
    }

    override fun authenticateUser(profileId: String?, promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun authenticateUserImplicitly(profileId: String?, promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun authenticateDeviceForResource(resourcePath: String, promise: Promise) {
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

    override fun registerUser(identityProviderId: String?, promise: Promise) {
        registerUserUseCase(identityProviderId, promise)
    }

    override fun deregisterUser(profileId: String?, promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun cancelRegistration() {
        TODO("Not yet implemented")
    }

    override fun getRedirectUri(promise: Promise) {
        getRedirectUriUseCase(promise)
    }

    override fun handleRegistrationCallback(uri: String?) {
        TODO("Not yet implemented")
    }

    override fun submitCustomRegistrationAction(customAction: String, identityProviderId: String, token: String?) {
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

    override fun submitPinAction(flowString: String?, action: String, pin: String?) {
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
