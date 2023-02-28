package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticatorRegistrationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiAuthenticatorRegistrationError
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.clean.use_cases.RegisterAuthenticatorUseCase
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError
import com.onegini.mobile.sdk.reactnative.managers.AuthenticatorManager
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class RegisterAuthenticatorUseCaseTests {

    @get:Rule
    val reactArgumentsTestRule = ReactArgumentsTestRule()

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    @Mock
    lateinit var authenticatorRegistrationError: OneginiAuthenticatorRegistrationError

    lateinit var registerAuthenticatorUseCase: RegisterAuthenticatorUseCase

    lateinit var authenticatorManager: AuthenticatorManager

    private val existingProfileId = "123456"
    private val nonExistingAuthenticator = "nonExistingAuthenticatorId"

    @Before
    fun setup() {
        authenticatorManager = AuthenticatorManager(oneginiSdk)
        registerAuthenticatorUseCase = RegisterAuthenticatorUseCase(oneginiSdk, authenticatorManager)
    }

    @Test
    fun `When no profile is authenticated, Then it should reject with a NO_PROFILE_AUTHENTICATED error`() {
        whenNoProfileAuthenticated()
        registerAuthenticatorUseCase(TestData.authenticator1.id, promiseMock)
        verify(promiseMock).reject(OneginiWrapperError.NO_PROFILE_AUTHENTICATED.code.toString(), OneginiWrapperError.NO_PROFILE_AUTHENTICATED.message)
    }

    @Test
    fun `When profile is authenticated but authenticator does not exist, Then it should reject with a AUTHENTICATOR_DOES_NOT_EXIST error`() {
        whenProfileAuthenticated()
        registerAuthenticatorUseCase(nonExistingAuthenticator, promiseMock)
        verify(promiseMock).reject(OneginiWrapperError.AUTHENTICATOR_DOES_NOT_EXIST.code.toString(), OneginiWrapperError.AUTHENTICATOR_DOES_NOT_EXIST.message)
    }

    @Test
    fun `When profile is authenticated and authenticator exists, Then it should call userclient_registerAuthenticator with the correct authenticator`() {
        whenProfileAuthenticated()
        whenAuthenticatorExists()
        registerAuthenticatorUseCase(TestData.authenticator2.id, promiseMock)
        verify(oneginiSdk.oneginiClient.userClient).registerAuthenticator(eq(TestData.authenticator2), any())
    }

    @Test
    fun `When onSuccess is called, Then the promise should resolve with null`() {
        whenProfileAuthenticated()
        whenAuthenticatorExists()
        Mockito.`when`(oneginiSdk.oneginiClient.userClient.registerAuthenticator(any(), any())).thenAnswer {
            it.getArgument<OneginiAuthenticatorRegistrationHandler>(1).onSuccess(null)
        }
        registerAuthenticatorUseCase(TestData.authenticator2.id, promiseMock)
        verify(promiseMock).resolve(null)
    }

    @Test
    fun `When onError is called, Then the promise should reject with the given error`() {
        whenProfileAuthenticated()
        whenAuthenticatorExists()
        Mockito.`when`(oneginiSdk.oneginiClient.userClient.registerAuthenticator(any(), any())).thenAnswer {
            it.getArgument<OneginiAuthenticatorRegistrationHandler>(1).onError(authenticatorRegistrationError)
        }
        registerAuthenticatorUseCase(TestData.authenticator2.id, promiseMock)
        verify(promiseMock).reject(authenticatorRegistrationError.errorType.toString(), authenticatorRegistrationError.message)

    }

    private fun whenNoProfileAuthenticated() {
        Mockito.`when`(oneginiSdk.oneginiClient.userClient.authenticatedUserProfile).thenReturn(null)
    }

    private fun whenProfileAuthenticated() {
        Mockito.`when`(oneginiSdk.oneginiClient.userClient.userProfiles).thenReturn(setOf(UserProfile(existingProfileId)))
    }

    private fun whenAuthenticatorExists() {
        Mockito.`when`(oneginiSdk.oneginiClient.userClient.getAllAuthenticators(any())).thenReturn(setOf(TestData.authenticator2))
    }
}
