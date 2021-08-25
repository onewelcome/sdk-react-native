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

    private lateinit var wrapper: OneginiSdkWrapper

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
            authenticateUserImplicitlyUseCase
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
        wrapper.registerUser("id1", JavaOnlyArray(), promiseMock)

        verify(registerUserUseCase).invoke("id1", JavaOnlyArray(), promiseMock)
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
}
