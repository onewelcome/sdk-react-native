package com.onegini.mobile

import com.facebook.react.bridge.*
import com.onegini.mobile.clean.use_cases.*
import com.onegini.mobile.clean.wrapper.OneginiSdkWrapper
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class OneginiSdkWrapperTests {

    @Mock
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var reactApplicationContext: ReactApplicationContext

    @Mock
    lateinit var promiseMock: Promise

    @Mock
    lateinit var startClientUseCase: StartClientUseCase

    @Mock
    lateinit var getIdentityProvidersUseCase: GetIdentityProvidersUseCase

    @Mock
    lateinit var getAccessTokenUseCase: GetAccessTokenUseCase

    @Mock
    lateinit var registerUserUseCase: RegisterUserUseCase

    @Mock
    lateinit var getAuthenticatedUserProfileUseCase: GetAuthenticatedUserProfileUseCase

    @Mock
    lateinit var getAllAuthenticatorsUseCase: GetAllAuthenticatorsUseCase

    @Mock
    lateinit var getRegisteredAuthenticatorsUseCase: GetRegisteredAuthenticatorsUseCase

    @Mock
    lateinit var getUserProfilesUseCase: GetUserProfilesUseCase

    @Mock
    lateinit var getRedirectUriUseCase: GetRedirectUriUseCase

    @Mock
    lateinit var deregisterUserUseCase: DeregisterUserUseCase

    @Mock
    lateinit var authenticateUserUseCase: AuthenticateUserUseCase

    @Mock
    lateinit var authenticateUserImplicitlyUseCase: AuthenticateUserImplicitlyUseCase

    @Mock
    lateinit var authenticateDeviceForResourceUseCase: AuthenticateDeviceForResourceUseCase

    @Mock
    lateinit var logoutUseCase: LogoutUseCase

    @Mock
    lateinit var resourceRequestUseCase: ResourceRequestUseCase

    @Mock
    lateinit var handleMobileAuthWithOtpUseCase: HandleMobileAuthWithOtpUseCase

    @Mock
    lateinit var startSingleSignOnUseCase: StartSingleSignOnUseCase

    @Mock
    lateinit var enrollMobileAuthenticationUseCase: EnrollMobileAuthenticationUseCase

    @Mock
    lateinit var registerAuthenticatorUseCase: RegisterAuthenticatorUseCase

    @Mock
    lateinit var isAuthenticatorRegisteredUseCase: IsAuthenticatorRegisteredUseCase

    @Mock
    lateinit var deregisterAuthenticatorUseCase: DeregisterAuthenticatorUseCase

    @Mock
    lateinit var setPreferredAuthenticatorUseCase: SetPreferredAuthenticatorUseCase

    @Mock
    lateinit var handleRegistrationCallbackUseCase: HandleRegistrationCallbackUseCase

    @Mock
    lateinit var cancelRegistrationUseCase: CancelRegistrationUseCase

    @Mock
    lateinit var submitCustomRegistrationActionUseCase: SubmitCustomRegistrationActionUseCase

    @Mock
    lateinit var acceptAuthenticationRequestUseCase: AcceptAuthenticationRequestUseCase

    //

    lateinit var wrapper: OneginiSdkWrapper

    @Before
    fun setup() {
        clearAllMocks()

        mockkStatic(Arguments::class)
        every { Arguments.createArray() } answers { JavaOnlyArray() }
        every { Arguments.createMap() } answers { JavaOnlyMap() }

        wrapper = OneginiSdkWrapper(
            oneginiSdk,
            reactApplicationContext,
            startClientUseCase,
            getIdentityProvidersUseCase,
            getAccessTokenUseCase,
            registerUserUseCase,
            getAuthenticatedUserProfileUseCase,
            getAllAuthenticatorsUseCase,
            getRegisteredAuthenticatorsUseCase,
            getUserProfilesUseCase,
            getRedirectUriUseCase,
            deregisterUserUseCase,
            authenticateUserUseCase,
            authenticateUserImplicitlyUseCase,
            authenticateDeviceForResourceUseCase,
            logoutUseCase,
            resourceRequestUseCase,
            handleMobileAuthWithOtpUseCase,
            startSingleSignOnUseCase,
            enrollMobileAuthenticationUseCase,
            registerAuthenticatorUseCase,
            isAuthenticatorRegisteredUseCase,
            deregisterAuthenticatorUseCase,
            setPreferredAuthenticatorUseCase,
            handleRegistrationCallbackUseCase,
            cancelRegistrationUseCase,
            submitCustomRegistrationActionUseCase,
            acceptAuthenticationRequestUseCase
        )
    }

    @Test
    fun `when startClient method is called calls startClientUseCase with proper params`() {
        wrapper.startClient(JavaOnlyMap(), promiseMock)

        verify(startClientUseCase).invoke(JavaOnlyMap(), promiseMock)
    }

    @Test
    fun `when getIdentityProviders method is called calls getIdentityProvidersUseCase with proper params`() {
        wrapper.getIdentityProviders(promiseMock)

        verify(getIdentityProvidersUseCase).invoke(promiseMock)
    }

    @Test
    fun `when getAccessToken method is called calls getAccessTokenUseCase with proper params`() {
        wrapper.getAccessToken(promiseMock)

        verify(getAccessTokenUseCase).invoke(promiseMock)
    }

    @Test
    fun `when registerUser method is called calls registerUserUseCase with proper params`() {
        wrapper.registerUser("id1", promiseMock)

        verify(registerUserUseCase).invoke("id1", promiseMock)
    }

    @Test
    fun `when getAuthenticatedUserProfile method is called calls getAuthenticatedUserProfileUseCase with proper params`() {
        wrapper.getAuthenticatedUserProfileUseCase(promiseMock)

        verify(getAuthenticatedUserProfileUseCase).invoke(promiseMock)
    }

    @Test
    fun `when getAllAuthenticators method is called calls getAllAuthenticatorsUseCase with proper params`() {
        wrapper.getAllAuthenticators("123456", promiseMock)

        verify(getAllAuthenticatorsUseCase).invoke("123456", promiseMock)
    }

    @Test
    fun `when getRegisteredAuthenticators method is called calls getRegisteredAuthenticatorsUseCase with proper params`() {
        wrapper.getRegisteredAuthenticators("123456", promiseMock)

        verify(getRegisteredAuthenticatorsUseCase).invoke("123456", promiseMock)
    }

    @Test
    fun `when getUserProfiles method is called calls getUserProfilesUseCase with proper params`() {
        wrapper.getUserProfiles(promiseMock)

        verify(getUserProfilesUseCase).invoke(promiseMock)
    }

    @Test
    fun `when getRedirectUri method is called calls getRedirectUriUseCase with proper params`() {
        wrapper.getRedirectUri(promiseMock)

        verify(getRedirectUriUseCase).invoke(promiseMock)
    }

    @Test
    fun `when deregisterUser method is called calls deregisterUserUseCase with proper params`() {
        wrapper.deregisterUser("123456", promiseMock)

        verify(deregisterUserUseCase).invoke("123456", promiseMock)
    }

    @Test
    fun `when authenticateUser method is called calls authenticateUserUseCase with proper params`() {
        wrapper.authenticateUser("123456", promiseMock)

        verify(authenticateUserUseCase).invoke("123456", promiseMock)
    }

    @Test
    fun `when authenticateUserImplicitly method is called calls authenticateUserImplicitlyUseCase with proper params`() {
        wrapper.authenticateUserImplicitly("123456", promiseMock)

        verify(authenticateUserImplicitlyUseCase).invoke("123456", promiseMock)
    }

    @Test
    fun `when authenticateDeviceForResource method is called calls authenticateDeviceForResourceUseCase with proper params`() {
        wrapper.authenticateDeviceForResource("path", promiseMock)

        verify(authenticateDeviceForResourceUseCase).invoke("path", promiseMock)
    }

    @Test
    fun `when logout method is called calls logoutUseCase with proper params`() {
        wrapper.logout(promiseMock)

        verify(logoutUseCase).invoke(promiseMock)
    }

    @Test
    fun `when resourceRequest method is called calls resourceRequestUseCase with proper params`() {
        wrapper.resourceRequest("type", JavaOnlyMap(), promiseMock)

        verify(resourceRequestUseCase).invoke("type", JavaOnlyMap(), promiseMock)
    }

    @Test
    fun `when handleMobileAuthWithOtp method is called calls handleMobileAuthWithOtpUseCase with proper params`() {
        wrapper.handleMobileAuthWithOtp("code123", promiseMock)

        verify(handleMobileAuthWithOtpUseCase).invoke("code123", promiseMock)
    }

    @Test
    fun `when startSingleSignOn method is called calls startSingleSignOnUseCase with proper params`() {
        wrapper.startSingleSignOn("http://url.pl", promiseMock)

        verify(startSingleSignOnUseCase).invoke("http://url.pl", promiseMock)
    }

    @Test
    fun `when enrollMobileAuthentication method is called calls enrollMobileAuthenticationUseCase with proper params`() {
        wrapper.enrollMobileAuthentication(promiseMock)

        verify(enrollMobileAuthenticationUseCase).invoke(promiseMock)
    }

    @Test
    fun `when registerAuthenticator method is called calls registerAuthenticatorUseCase with proper params`() {
        wrapper.registerAuthenticator("profileId", "Fingerprint", promiseMock)

        verify(registerAuthenticatorUseCase).invoke("profileId", "Fingerprint", promiseMock)
    }

    @Test
    fun `when isAuthenticatorRegistered method is called calls isAuthenticatorRegisteredUseCase with proper params`() {
        wrapper.isAuthenticatorRegistered("profileId", "Fingerprint", promiseMock)

        verify(isAuthenticatorRegisteredUseCase).invoke("profileId", "Fingerprint", promiseMock)
    }

    @Test
    fun `when deregisterAuthenticator method is called calls deregisterAuthenticatorUseCase with proper params`() {
        wrapper.deregisterAuthenticator("profileId", "Fingerprint", promiseMock)

        verify(deregisterAuthenticatorUseCase).invoke("profileId", "Fingerprint", promiseMock)
    }

    @Test
    fun `when setPreferredAuthenticator method is called calls setPreferredAuthenticatorUseCase with proper params`() {
        wrapper.setPreferredAuthenticator("profileId", "id1", promiseMock)

        verify(setPreferredAuthenticatorUseCase).invoke("profileId", "id1", promiseMock)
    }

    @Test
    fun `when handleRegistrationCallback method is called calls handleRegistrationCallbackUseCase with proper params`() {
        wrapper.handleRegistrationCallback("http://www.pl")

        verify(handleRegistrationCallbackUseCase).invoke("http://www.pl")
    }

    @Test
    fun `when cancelRegistration method is called calls cancelRegistrationUseCase with proper params`() {
        wrapper.cancelRegistration()

        verify(cancelRegistrationUseCase).invoke()
    }

    @Test
    fun `when submitCustomRegistrationAction method is called calls submitCustomRegistrationActionUseCase with proper params`() {
        wrapper.submitCustomRegistrationAction("action", "id1", "token")

        verify(submitCustomRegistrationActionUseCase).invoke("action", "id1", "token")
    }

    @Test
    fun `when acceptAuthenticationRequest method is called calls acceptAuthenticationRequestUseCase with proper params`() {
        wrapper.acceptAuthenticationRequest("Pin", "1234")

        verify(acceptAuthenticationRequestUseCase).invoke("Pin", "1234")
    }
}
