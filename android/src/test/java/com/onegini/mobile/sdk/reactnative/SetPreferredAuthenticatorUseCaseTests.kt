package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.clean.use_cases.SetPreferredAuthenticatorUseCase
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.AUTHENTICATOR_DOES_NOT_EXIST
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.PROFILE_DOES_NOT_EXIST
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
    fun `When userProfile does not exist, Then should reject with PROFILE_DOES_NOT_EXIST error`() {
        whenProfileDoesNotExist()
        setPreferredAuthenticatorUseCase(profileId, TestData.authenticator1.id, promiseMock)
        verify(promiseMock).reject(PROFILE_DOES_NOT_EXIST.code.toString(), PROFILE_DOES_NOT_EXIST.message)
    }

    @Test
    fun `When userProfile exists but authenticator does not, Then should reject with AUTHENTICATOR_DOES_NOT_EXIST error`() {
        whenProfileExists()
        whenAuthenticatorDoesNotExist()
        setPreferredAuthenticatorUseCase(profileId, TestData.authenticator1.id, promiseMock)
        verify(promiseMock).reject(AUTHENTICATOR_DOES_NOT_EXIST.code.toString(), AUTHENTICATOR_DOES_NOT_EXIST.message)
    }

    @Test
    fun `When profile and authenticator exist, Then should resolve with null`() {
        whenProfileExists()
        whenAuthenticatorExists()
        setPreferredAuthenticatorUseCase(profileId, TestData.authenticator1.id, promiseMock)
        verify(promiseMock).resolve(null)
    }

    private fun whenProfileExists() {
        `when`(oneginiSdk.oneginiClient.userClient.userProfiles).thenReturn(setOf(UserProfile(profileId)))
    }

    private fun whenProfileDoesNotExist() {
        `when`(oneginiSdk.oneginiClient.userClient.userProfiles).thenReturn(setOf())
    }

    private fun whenAuthenticatorDoesNotExist() {
        `when`(oneginiSdk.oneginiClient.userClient.getRegisteredAuthenticators(any())).thenReturn(setOf())
    }

    private fun whenAuthenticatorExists() {
        `when`(oneginiSdk.oneginiClient.userClient.getRegisteredAuthenticators(any())).thenReturn(setOf(TestData.authenticator1))
    }
}
