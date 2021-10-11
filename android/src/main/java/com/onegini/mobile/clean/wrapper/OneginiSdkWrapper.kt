package com.onegini.mobile.clean.wrapper

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.clean.use_cases.*

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
    val getUserProfilesUseCase: GetUserProfilesUseCase = GetUserProfilesUseCase(oneginiSDK),
    val getRedirectUriUseCase: GetRedirectUriUseCase = GetRedirectUriUseCase(oneginiSDK),
    val deregisterUserUseCase: DeregisterUserUseCase = DeregisterUserUseCase(oneginiSDK),
    val authenticateUserUseCase: AuthenticateUserUseCase = AuthenticateUserUseCase(oneginiSDK),
    val authenticateUserImplicitlyUseCase: AuthenticateUserImplicitlyUseCase = AuthenticateUserImplicitlyUseCase(oneginiSDK),
    val authenticateDeviceForResourceUseCase: AuthenticateDeviceForResourceUseCase = AuthenticateDeviceForResourceUseCase(oneginiSDK),
    val logoutUseCase: LogoutUseCase = LogoutUseCase(oneginiSDK),
    val resourceRequestUseCase: ResourceRequestUseCase = ResourceRequestUseCase(oneginiSDK),
    val handleMobileAuthWithOtpUseCase: HandleMobileAuthWithOtpUseCase = HandleMobileAuthWithOtpUseCase(oneginiSDK),
    val startSingleSignOnUseCase: StartSingleSignOnUseCase = StartSingleSignOnUseCase(oneginiSDK),
    val enrollMobileAuthenticationUseCase: EnrollMobileAuthenticationUseCase = EnrollMobileAuthenticationUseCase(oneginiSDK),
    val registerAuthenticatorUseCase: RegisterAuthenticatorUseCase = RegisterAuthenticatorUseCase(oneginiSDK)
) : IOneginiSdkWrapper {

    override fun startClient(rnConfig: ReadableMap, promise: Promise) {
        startClientUseCase(rnConfig, promise)
    }

    override fun authenticateUser(profileId: String?, authenticatorId: String?, promise: Promise) {
        authenticateUserUseCase(profileId, authenticatorId, promise)
    }

    override fun authenticateUserImplicitly(profileId: String?, scopes: ReadableArray, promise: Promise) {
        authenticateUserImplicitlyUseCase(profileId, scopes, promise)
    }

    override fun authenticateDeviceForResource(resourcePath: String, promise: Promise) {
        authenticateDeviceForResourceUseCase(resourcePath, promise)
    }

    override fun getUserProfiles(promise: Promise) {
        getUserProfilesUseCase(promise)
    }

    override fun logout(promise: Promise) {
        logoutUseCase(promise)
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

    override fun registerAuthenticator(profileId: String, type: String, promise: Promise) {
        registerAuthenticatorUseCase(profileId, type, promise)
    }

    override fun isAuthenticatorRegistered(profileId: String, type: String, promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun deregisterAuthenticator(profileId: String, type: String, promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun registerFingerprintAuthenticator(profileId: String, promise: Promise) {
        // TODO: to remove
    }

    override fun isFingerprintAuthenticatorRegistered(profileId: String, promise: Promise) {
        // TODO: to remove
    }

    override fun deregisterFingerprintAuthenticator(profileId: String, promise: Promise) {
        // TODO: to remove
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
        enrollMobileAuthenticationUseCase(promise)
    }

    override fun acceptMobileAuthConfirmation(promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun denyMobileAuthConfirmation(promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun handleMobileAuthWithOtp(otpCode: String, promise: Promise) {
        handleMobileAuthWithOtpUseCase(otpCode, promise)
    }

    override fun resourceRequest(type: String, details: ReadableMap, promise: Promise) {
        resourceRequestUseCase(type, details, promise)
    }

    override fun startSingleSignOn(url: String, promise: Promise) {
        startSingleSignOnUseCase(url, promise)
    }

    //

    fun clear() {
        resourceRequestUseCase.dispose()
    }
}
