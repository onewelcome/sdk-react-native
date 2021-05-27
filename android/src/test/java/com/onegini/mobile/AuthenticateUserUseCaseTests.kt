package com.onegini.mobile

import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.Promise
import com.onegini.mobile.clean.use_cases.AuthenticateUserUseCase
import com.onegini.mobile.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiAuthenticationError
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
import org.mockito.Mockito.lenient
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

    @Before
    fun prepareIdentityProviders() {
        lenient().`when`(authenticationError.errorType).thenReturn(666)
        lenient().`when`(authenticationError.message).thenReturn("MyError")
    }

    //

    @Test
    fun `when user profile cannot be created should reject with proper errors`() {
        AuthenticateUserUseCase(oneginiSdk)("123", promiseMock)

        verify(promiseMock).reject(OneginiWrapperErrors.USER_PROFILE_IS_NULL.code, OneginiWrapperErrors.USER_PROFILE_IS_NULL.message)
    }

    @Test
    fun `when successful should resolve with user profile and custom info`() {
        `when`(oneginiSdk.oneginiClient.userClient.authenticateUser(any(), any())).thenAnswer {
            it.getArgument<OneginiAuthenticationHandler>(1).onSuccess(UserProfile("666666"), CustomInfo(666, "customData"))
        }

        AuthenticateUserUseCase(oneginiSdk)("123456", promiseMock)

        argumentCaptor<JavaOnlyMap> {
            verify(promiseMock).resolve(capture())

            Assert.assertEquals("666666", firstValue.getMap("userProfile")?.getString("profileId"))
            Assert.assertEquals("customData", firstValue.getMap("customInfo")?.getString("data"))
            Assert.assertEquals(666, firstValue.getMap("customInfo")?.getInt("status"))
        }
    }

    @Test
    fun `when fails should reject with proper error`() {
        `when`(oneginiSdk.oneginiClient.userClient.authenticateUser(any(), any())).thenAnswer {
            it.getArgument<OneginiAuthenticationHandler>(1).onError(authenticationError)
        }

        AuthenticateUserUseCase(oneginiSdk)("123456", promiseMock)

        argumentCaptor<UserProfile> {
            verify(oneginiSdk.oneginiClient.userClient).authenticateUser(capture(), any())

            Assert.assertEquals("123456", firstValue.profileId)
        }

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals("666", firstValue)
            Assert.assertEquals("MyError", secondValue)
        }
    }
}
