package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetAccessTokenUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetAuthenticatedUserProfileUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetIdentityProvidersUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.RegisterUserUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.StartClientUseCase
import com.onegini.mobile.sdk.reactnative.clean.wrapper.OneginiSdkWrapper
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
            getAuthenticatedUserProfileUseCase
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
}
