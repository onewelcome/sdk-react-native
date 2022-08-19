package com.onegini.mobile

import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.clean.use_cases.AuthenticateUserUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetRegisteredAuthenticatorsUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetUserProfileUseCase
import com.onegini.mobile.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiAuthenticationError
import com.onegini.mobile.sdk.android.model.OneginiAuthenticator
import com.onegini.mobile.sdk.android.model.entity.CustomInfo
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class AuthenticateUserUseCaseTests {

    @get:Rule
    val reactArgumentsTestRule = ReactArgumentsTestRule()

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    @Mock
    lateinit var authenticationError: OneginiAuthenticationError

    @Mock
    lateinit var getRegisteredAuthenticatorsUseCase: GetRegisteredAuthenticatorsUseCase

    @Mock
    lateinit var authenticator: OneginiAuthenticator

    @Mock
    lateinit var getUserProfileUseCase: GetUserProfileUseCase

    private lateinit var authenticateUserUseCase: AuthenticateUserUseCase

    @Before
    fun setup() {
        `when`(getUserProfileUseCase.invoke(any())).thenReturn(UserProfile("123456"))
        `when`(authenticator.id).thenReturn("1")

        authenticateUserUseCase = AuthenticateUserUseCase(oneginiSdk, getRegisteredAuthenticatorsUseCase, getUserProfileUseCase)
    }

    @Test
    fun `when user profile cannot be created should reject with proper errors`() {
        `when`(getUserProfileUseCase.invoke(any())).thenReturn(null)

        authenticateUserUseCase("123", "1", promiseMock)

        verify(promiseMock).reject(OneginiWrapperErrors.USER_PROFILE_IS_NULL.code, OneginiWrapperErrors.USER_PROFILE_IS_NULL.message)
    }

    @Test
    fun `when successful should resolve with user profile and custom info`() {
        `when`(oneginiSdk.oneginiClient.userClient.authenticateUser(any(), any())).thenAnswer {
            it.getArgument<OneginiAuthenticationHandler>(1).onSuccess(UserProfile("666666"), CustomInfo(666, "customData"))
        }

        authenticateUserUseCase("123456", null, promiseMock)

        argumentCaptor<JavaOnlyMap> {
            verify(promiseMock).resolve(capture())

            Assert.assertEquals("666666", firstValue.getMap("userProfile")?.getString("profileId"))
            Assert.assertEquals("customData", firstValue.getMap("customInfo")?.getString("data"))
            Assert.assertEquals(666, firstValue.getMap("customInfo")?.getInt("status"))
        }
    }

    @Test
    fun `should call SDK method with proper id and authenticatorId`() {
        `when`(getRegisteredAuthenticatorsUseCase.getList(any())).thenReturn(setOf(authenticator))

        authenticateUserUseCase("123456", "1", promiseMock)

        argumentCaptor<UserProfile, OneginiAuthenticator>().apply {
            verify(oneginiSdk.oneginiClient.userClient).authenticateUser(first.capture(), second.capture(), any())

            Assert.assertEquals("123456", first.firstValue.profileId)
            Assert.assertEquals("1", second.firstValue.id)
        }
    }

    @Test
    fun `when fails should reject with proper error`() {
        whenAuthenticateUserFailed()

        authenticateUserUseCase("123456", null, promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals("666", firstValue)
            Assert.assertEquals("MyError", secondValue)
        }
    }

    private fun whenAuthenticateUserFailed() {
        `when`(authenticationError.errorType).thenReturn(666)
        `when`(authenticationError.message).thenReturn("MyError")
        `when`(oneginiSdk.oneginiClient.userClient.authenticateUser(any(), any())).thenAnswer {
            it.getArgument<OneginiAuthenticationHandler>(1).onError(authenticationError)
        }
    }
}
