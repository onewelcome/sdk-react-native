package com.onegini.mobile

import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableArray
import com.onegini.mobile.clean.use_cases.AuthenticateUserImplicitlyUseCase
import com.onegini.mobile.clean.use_cases.GetUserProfileUseCase
import com.onegini.mobile.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.android.handlers.OneginiImplicitAuthenticationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiImplicitTokenRequestError
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class AuthenticateUserImplicitlyUseCaseTests {

    @get:Rule
    val reactArgumentsTestRule = ReactArgumentsTestRule()

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    lateinit var scopes: ReadableArray

    @Mock
    lateinit var authenticationError: OneginiImplicitTokenRequestError

    @Mock
    lateinit var getUserProfileUseCase: GetUserProfileUseCase

    @Before
    fun setup() {
        Mockito.`when`(getUserProfileUseCase.invoke(any())).thenReturn(UserProfile("123456"))

        scopes = JavaOnlyArray.of("read")
    }

    @Test
    fun `when no profile is found should reject with error`() {
        AuthenticateUserImplicitlyUseCase(oneginiSdk)("444333", scopes, promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals(OneginiWrapperErrors.USER_PROFILE_IS_NULL.code, firstValue)
            Assert.assertEquals(OneginiWrapperErrors.USER_PROFILE_IS_NULL.message, secondValue)
        }
    }

    @Test
    fun `when successful should resolve with null`() {
        Mockito.`when`(oneginiSdk.oneginiClient.userClient.authenticateUserImplicitly(any(), any(), any())).thenAnswer {
            it.getArgument<OneginiImplicitAuthenticationHandler>(2).onSuccess(UserProfile("123456"))
        }

        AuthenticateUserImplicitlyUseCase(oneginiSdk, getUserProfileUseCase)("123456", scopes, promiseMock)

        verify(promiseMock).resolve(null)
    }

    @Test
    fun `when fails should reject with proper error`() {
        Mockito.`when`(authenticationError.errorType).thenReturn(666)
        Mockito.`when`(authenticationError.message).thenReturn("MyError")

        Mockito.`when`(oneginiSdk.oneginiClient.userClient.authenticateUserImplicitly(any(), any(), any())).thenAnswer {
            it.getArgument<OneginiImplicitAuthenticationHandler>(2).onError(authenticationError)
        }

        AuthenticateUserImplicitlyUseCase(oneginiSdk, getUserProfileUseCase)("123456", scopes, promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals("666", firstValue)
            Assert.assertEquals("MyError", secondValue)
        }
    }

    @Test
    fun `when called should call proper methods`() {
        Mockito.`when`(authenticationError.errorType).thenReturn(666)
        Mockito.`when`(authenticationError.message).thenReturn("MyError")

        Mockito.`when`(oneginiSdk.oneginiClient.userClient.authenticateUserImplicitly(any(), any(), any())).thenAnswer {
            it.getArgument<OneginiImplicitAuthenticationHandler>(2).onError(authenticationError)
        }

        AuthenticateUserImplicitlyUseCase(oneginiSdk, getUserProfileUseCase)("123456", scopes, promiseMock)

        argumentCaptor<UserProfile> {
            verify(oneginiSdk.oneginiClient.userClient).authenticateUserImplicitly(capture(), any(), any())

            Assert.assertEquals("123456", firstValue.profileId)
        }
    }
}
