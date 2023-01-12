package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticatorDeregistrationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiAuthenticatorDeregistrationError
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.clean.use_cases.DeregisterAuthenticatorUseCase
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.AUTHENTICATOR_DOES_NOT_EXIST
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.AUTHENTICATOR_NOT_REGISTERED
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.NO_PROFILE_AUTHENTICATED
import com.onegini.mobile.sdk.reactnative.managers.AuthenticatorManager
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class DeregisterAuthenticatorUseCaseTests {

    @get:Rule
    val reactArgumentsTestRule = ReactArgumentsTestRule()

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    @Mock
    lateinit var deregistrationError: OneginiAuthenticatorDeregistrationError

    lateinit var deregisterAuthenticatorUseCase: DeregisterAuthenticatorUseCase
    lateinit var authenticatorManager: AuthenticatorManager

    private val existingProfileId = "123456"
    private val nonExistingAuthenticator = "nonExistingAuthenticatorId"

    @Before
    fun setup() {
        authenticatorManager = AuthenticatorManager(oneginiSdk)
        deregisterAuthenticatorUseCase = DeregisterAuthenticatorUseCase(oneginiSdk, authenticatorManager)
    }

    @Test
    fun `When calling deregisterAuthenticator while no profile is authenticated, Then it should reject with NO_PROFILE_AUTHENTICATED`() {
        whenNoProfileAuthenticated()
        deregisterAuthenticatorUseCase(TestData.authenticator1.id, promiseMock)
        verify(promiseMock).reject(NO_PROFILE_AUTHENTICATED.code.toString(), NO_PROFILE_AUTHENTICATED.message)
    }

    @Test
    fun `When profile authenticated but authenticator does not exist, Then it should reject with AUTHENTICATOR_DOES_NOT_EXIST`() {
        whenProfileAuthenticated()
        deregisterAuthenticatorUseCase(nonExistingAuthenticator, promiseMock)
        verify(promiseMock).reject(AUTHENTICATOR_DOES_NOT_EXIST.code.toString(), AUTHENTICATOR_DOES_NOT_EXIST.message)
    }

    @Test
    fun `When profile authenticated and authenticator exists, but is not registered, Then it should reject with AUTHENTICATOR_NOT_REGISTERED`() {
        whenProfileAuthenticated()
        whenAuthenticatorExistsButNotRegistered()
        deregisterAuthenticatorUseCase(TestData.authenticator2.id, promiseMock)
        verify(promiseMock).reject(AUTHENTICATOR_NOT_REGISTERED.code.toString(), AUTHENTICATOR_NOT_REGISTERED.message)
    }

    @Test
    fun `When profile authenticated and authenticator exists + registered, Then it should call userClient_deregisterAuthenticator with the corresponding authenticator`() {
        whenProfileAuthenticated()
        whenAuthenticatorRegistered()
        deregisterAuthenticatorUseCase(TestData.authenticator1.id, promiseMock)
        verify(oneginiSdk.oneginiClient.userClient).deregisterAuthenticator(eq(TestData.authenticator1), any())
    }

    @Test
    fun `When deregisterAuthenticator calls onSuccess, Then it should resolve with null`() {
        whenProfileAuthenticated()
        whenAuthenticatorRegistered()
        `when`(oneginiSdk.oneginiClient.userClient.deregisterAuthenticator(any(), any())).thenAnswer {
            it.getArgument<OneginiAuthenticatorDeregistrationHandler>(1).onSuccess()
        }
        deregisterAuthenticatorUseCase(TestData.authenticator1.id, promiseMock)
        verify(promiseMock).resolve(null)
    }

    @Test
    fun `When deregisterAuthenticator calls onError, Then it should reject with the error's code and message`() {
        whenProfileAuthenticated()
        whenAuthenticatorRegistered()
        `when`(oneginiSdk.oneginiClient.userClient.deregisterAuthenticator(any(), any())).thenAnswer {
            it.getArgument<OneginiAuthenticatorDeregistrationHandler>(1).onError(deregistrationError)
        }
        deregisterAuthenticatorUseCase(TestData.authenticator1.id, promiseMock)
        verify(promiseMock).reject(deregistrationError.errorType.toString(), deregistrationError.message)
    }

    private fun whenNoProfileAuthenticated() {
        `when`(oneginiSdk.oneginiClient.userClient.authenticatedUserProfile).thenReturn(null)
    }

    private fun whenProfileAuthenticated() {
        `when`(oneginiSdk.oneginiClient.userClient.userProfiles).thenReturn(setOf(UserProfile(existingProfileId)))
    }

    private fun whenAuthenticatorExistsButNotRegistered() {
        `when`(oneginiSdk.oneginiClient.userClient.getAllAuthenticators(any())).thenReturn(setOf(TestData.authenticator2))
    }

    private fun whenAuthenticatorRegistered() {
        `when`(oneginiSdk.oneginiClient.userClient.getAllAuthenticators(any())).thenReturn(setOf(TestData.authenticator1))
    }
}
