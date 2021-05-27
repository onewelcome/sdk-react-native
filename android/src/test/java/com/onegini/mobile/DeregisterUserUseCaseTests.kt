package com.onegini.mobile

import com.facebook.react.bridge.Promise
import com.onegini.mobile.clean.use_cases.DeregisterUserUseCase
import com.onegini.mobile.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.android.handlers.OneginiDeregisterUserProfileHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiDeregistrationError
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
class DeregisterUserUseCaseTests {

    @get:Rule
    val reactArgumentsTestRule = ReactArgumentsTestRule()

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    @Mock
    lateinit var deregistrationError: OneginiDeregistrationError

    @Before
    fun prepareIdentityProviders() {
        lenient().`when`(deregistrationError.errorType).thenReturn(666)
        lenient().`when`(deregistrationError.message).thenReturn("MyError")
    }

    //

    @Test
    fun `when user profile cannot be created should reject with proper errors`() {
        DeregisterUserUseCase(oneginiSdk)("123", promiseMock)

        verify(promiseMock).reject(OneginiWrapperErrors.USER_PROFILE_IS_NULL.code, OneginiWrapperErrors.USER_PROFILE_IS_NULL.message)
    }

    @Test
    fun `when fails should reject with proper error`() {
        `when`(oneginiSdk.oneginiClient.userClient.deregisterUser(any(), any())).thenAnswer {
            it.getArgument<OneginiDeregisterUserProfileHandler>(1).onError(deregistrationError)
        }

        DeregisterUserUseCase(oneginiSdk)("123456", promiseMock)

        argumentCaptor<UserProfile> {
            verify(oneginiSdk.oneginiClient.userClient).deregisterUser(capture(), any())

            Assert.assertEquals("123456", firstValue.profileId)
        }

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals("666", firstValue)
            Assert.assertEquals("MyError", secondValue)
        }
    }

    @Test
    fun `when successful should resolve with null`() {
        `when`(oneginiSdk.oneginiClient.userClient.deregisterUser(any(), any())).thenAnswer {
            it.getArgument<OneginiDeregisterUserProfileHandler>(1).onSuccess()
        }

        DeregisterUserUseCase(oneginiSdk)("123456", promiseMock)

        argumentCaptor<UserProfile> {
            verify(oneginiSdk.oneginiClient.userClient).deregisterUser(capture(), any())

            Assert.assertEquals("123456", firstValue.profileId)
        }

        verify(promiseMock).resolve(null)
    }
}
