package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.clean.use_cases.SetPreferredAuthenticatorUseCase
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError.*
import com.onegini.mobile.sdk.reactnative.managers.AuthenticatorManager
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class SetPreferredAuthenticatorUseCaseTests {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    lateinit var setPreferredAuthenticatorUseCase: SetPreferredAuthenticatorUseCase

    lateinit var authenticatorManager: AuthenticatorManager


    private val profileId = "123456"

    @Before
    fun setup() {
        authenticatorManager = AuthenticatorManager(oneginiSdk)
        setPreferredAuthenticatorUseCase = SetPreferredAuthenticatorUseCase(authenticatorManager)
    }

    @Test
    fun `When no profile is authenticated, Then should reject with NO_PROFILE_AUTHENTICATED error`() {
        whenProfileNotAuthenticated()
        setPreferredAuthenticatorUseCase(TestData.authenticator1.id, promiseMock)
        verify(promiseMock).reject(NO_PROFILE_AUTHENTICATED.code.toString(), NO_PROFILE_AUTHENTICATED.message)
    }

    @Test
    fun `When profile authenticated but authenticator does not exist, Then should reject with AUTHENTICATOR_DOES_NOT_EXIST error`() {
        whenProfileAuthenticated()
        whenAuthenticatorDoesNotExist()
        setPreferredAuthenticatorUseCase(TestData.authenticator1.id, promiseMock)
        verify(promiseMock).reject(AUTHENTICATOR_DOES_NOT_EXIST.code.toString(), AUTHENTICATOR_DOES_NOT_EXIST.message)
    }

    @Test
    fun `When profile authenticated and authenticator exist, Then should resolve with null`() {
        whenProfileAuthenticated()
        whenAuthenticatorExists()
        setPreferredAuthenticatorUseCase(TestData.authenticator1.id, promiseMock)
        verify(promiseMock).resolve(null)
    }

    private fun whenProfileAuthenticated() {
        `when`(oneginiSdk.oneginiClient.userClient.authenticatedUserProfile).thenReturn(UserProfile(profileId))
    }

    private fun whenProfileNotAuthenticated() {
        `when`(oneginiSdk.oneginiClient.userClient.authenticatedUserProfile).thenReturn(null)
    }

    private fun whenAuthenticatorDoesNotExist() {
        `when`(oneginiSdk.oneginiClient.userClient.getRegisteredAuthenticators(any())).thenReturn(setOf())
    }

    private fun whenAuthenticatorExists() {
        `when`(oneginiSdk.oneginiClient.userClient.getRegisteredAuthenticators(any())).thenReturn(setOf(TestData.authenticator1))
    }
}
