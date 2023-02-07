package com.onegini.mobile.sdk.reactnative.clean.wrapper

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.onegini.mobile.sdk.reactnative.clean.use_cases.AcceptMobileAuthConfirmationUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.AuthenticateDeviceForResourceUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.AuthenticateUserImplicitlyUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.AuthenticateUserUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.CancelBrowserRegistrationUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.CancelCustomRegistrationUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.CancelPinAuthenticationUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.CancelPinCreationUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.ChangePinUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.DenyMobileAuthConfirmationUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.DeregisterAuthenticatorUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.DeregisterUserUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.EnrollMobileAuthenticationUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetAccessTokenUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetAllAuthenticatorsUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetAuthenticatedUserProfileUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetIdentityProvidersUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetRedirectUriUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetRegisteredAuthenticatorsUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetResourceBaseUrlUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetUserProfilesUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.HandleMobileAuthWithOtpUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.HandleRegistrationCallbackUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.LogoutUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.RegisterAuthenticatorUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.RegisterUserUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.ResourceRequestUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.SetPreferredAuthenticatorUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.StartClientUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.StartSingleSignOnUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.SubmitCustomRegistrationActionUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.SubmitPinUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.ValidatePinWithPolicyUseCase
import javax.inject.Inject

class OneginiSdkWrapper @Inject constructor(
    private val acceptMobileAuthConfirmationUseCase: AcceptMobileAuthConfirmationUseCase,
    private val authenticateDeviceForResourceUseCase: AuthenticateDeviceForResourceUseCase,
    private val authenticateUserImplicitlyUseCase: AuthenticateUserImplicitlyUseCase,
    private val authenticateUserUseCase: AuthenticateUserUseCase,
    private val cancelBrowserRegistrationUseCase: CancelBrowserRegistrationUseCase,
    private val cancelCustomRegistrationUseCase: CancelCustomRegistrationUseCase,
    private val cancelPinAuthenticationUseCase: CancelPinAuthenticationUseCase,
    private val cancelPinCreationUseCase: CancelPinCreationUseCase,
    private val changePinUseCase: ChangePinUseCase,
    private val deregisterAuthenticatorUseCase: DeregisterAuthenticatorUseCase,
    private val deregisterUserUseCase: DeregisterUserUseCase,
    private val denyMobileAuthConfirmationUseCase: DenyMobileAuthConfirmationUseCase,
    private val enrollMobileAuthenticationUseCase: EnrollMobileAuthenticationUseCase,
    private val getAccessTokenUseCase: GetAccessTokenUseCase,
    private val getAllAuthenticatorsUseCase: GetAllAuthenticatorsUseCase,
    private val getAuthenticatedUserProfileUseCase: GetAuthenticatedUserProfileUseCase,
    private val getIdentityProvidersUseCase: GetIdentityProvidersUseCase,
    private val getRedirectUriUseCase: GetRedirectUriUseCase,
    private val getRegisteredAuthenticatorsUseCase: GetRegisteredAuthenticatorsUseCase,
    private val getResourceBaseUrlUseCase: GetResourceBaseUrlUseCase,
    private val getUserProfilesUseCase: GetUserProfilesUseCase,
    private val handleMobileAuthWithOtpUseCase: HandleMobileAuthWithOtpUseCase,
    private val handleRegistrationCallbackUseCase: HandleRegistrationCallbackUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val registerAuthenticatorUseCase: RegisterAuthenticatorUseCase,
    private val registerUserUseCase: RegisterUserUseCase,
    private val resourceRequestUseCase: ResourceRequestUseCase,
    private val setPreferredAuthenticatorUseCase: SetPreferredAuthenticatorUseCase,
    private val startClientUseCase: StartClientUseCase,
    private val startSingleSignOnUseCase: StartSingleSignOnUseCase,
    private val submitCustomRegistrationActionUseCase: SubmitCustomRegistrationActionUseCase,
    private val submitPinUseCase: SubmitPinUseCase,
    private val validatePinWithPolicyUseCase: ValidatePinWithPolicyUseCase,
)  {

    fun startClient(rnConfig: ReadableMap, promise: Promise) {
        startClientUseCase(rnConfig, promise)
    }

    fun authenticateUser(profileId: String, authenticatorId: String?, promise: Promise) {
        authenticateUserUseCase(profileId, authenticatorId, promise)
    }

    fun authenticateUserImplicitly(profileId: String, scopes: ReadableArray?, promise: Promise) {
        authenticateUserImplicitlyUseCase(profileId, scopes, promise)
    }

    fun authenticateDeviceForResource(scopes: ReadableArray?, promise: Promise) {
        authenticateDeviceForResourceUseCase(scopes, promise)
    }

    fun getUserProfiles(promise: Promise) {
        getUserProfilesUseCase(promise)
    }

    fun logout(promise: Promise) {
        logoutUseCase(promise)
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

    fun setPreferredAuthenticator(authenticatorId: String, promise: Promise) {
        setPreferredAuthenticatorUseCase(authenticatorId, promise)
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

    fun deregisterAuthenticator(authenticatorId: String, promise: Promise) {
        deregisterAuthenticatorUseCase(authenticatorId, promise)
    }

    fun cancelPinCreation(promise: Promise) {
        cancelPinCreationUseCase(promise)
    }

    fun cancelPinAuthentication(promise: Promise) {
        cancelPinAuthenticationUseCase(promise)
    }

    fun cancelBrowserRegistration(promise: Promise) {
        cancelBrowserRegistrationUseCase(promise)
    }

    fun cancelCustomRegistration(message: String, promise: Promise) {
        cancelCustomRegistrationUseCase(message, promise)
    }

    fun getRedirectUri(promise: Promise) {
        getRedirectUriUseCase(promise)
    }

    fun handleRegistrationCallback(uri: String, promise: Promise) {
        handleRegistrationCallbackUseCase(uri, promise)
    }

    fun submitCustomRegistrationAction(identityProviderId: String, token: String?, promise: Promise) {
        submitCustomRegistrationActionUseCase(identityProviderId, token, promise)
    }

    fun getIdentityProviders(promise: Promise) {
        getIdentityProvidersUseCase(promise)
    }

    fun registerFingerprintAuthenticator(profileId: String, promise: Promise) {
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
        changePinUseCase(promise)
    }

    fun submitPin(pinFlow: String, pin: String, promise: Promise) {
        submitPinUseCase(pinFlow, pin, promise)
    }

    fun enrollMobileAuthentication(promise: Promise) {
        enrollMobileAuthenticationUseCase(promise)
    }

    fun acceptMobileAuthConfirmation(promise: Promise) {
        acceptMobileAuthConfirmationUseCase(promise)
    }

    fun denyMobileAuthConfirmation(promise: Promise) {
        denyMobileAuthConfirmationUseCase(promise)
    }

    fun handleMobileAuthWithOtp(otpCode: String, promise: Promise) {
        handleMobileAuthWithOtpUseCase(otpCode, promise)
    }

    fun resourceRequest(type: String, details: ReadableMap, promise: Promise) {
        resourceRequestUseCase(type, details, promise)
    }

    fun startSingleSignOn(url: String, promise: Promise) {
        startSingleSignOnUseCase(url, promise)
    }

    fun registerAuthenticator(authenticatorId: String, promise: Promise) {
        registerAuthenticatorUseCase(authenticatorId, promise)
    }

    fun getResourceBaseUrl(promise: Promise) {
        getResourceBaseUrlUseCase(promise)
    }
}
