package com.onegini.mobile

import com.onegini.mobile.clean.use_cases.DeregisterUserUseCase
import com.onegini.mobile.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.android.handlers.OneginiDeregisterUserProfileHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiDeregistrationError
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.lenient
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class DeregisterUserUseCaseTests : BaseTests() {

    @Mock
    lateinit var deregistrationError: OneginiDeregistrationError

    @Before
    fun prepareIdentityProviders() {
        lenient().`when`(deregistrationError.errorType).thenReturn(666)
        lenient().`when`(deregistrationError.message).thenReturn("MyError")
    }

    //

    @Test
    fun `when user profile cannot be created rejects with proper errors`() {
        DeregisterUserUseCase()("123", promiseMock)

        verify(promiseMock).reject(OneginiWrapperErrors.USER_PROFILE_IS_NULL.code, OneginiWrapperErrors.USER_PROFILE_IS_NULL.message)
    }

    @Test
    fun `when fails rejects with proper error`() {
        `when`(userClient.deregisterUser(any(), any())).thenAnswer {
            it.getArgument<OneginiDeregisterUserProfileHandler>(1).onError(deregistrationError)
        }

        DeregisterUserUseCase()("123456", promiseMock)

        argumentCaptor<UserProfile> {
            verify(userClient).deregisterUser(capture(), any())

            Assert.assertEquals("123456", firstValue.profileId)
        }

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals("666", firstValue)
            Assert.assertEquals("MyError", secondValue)
        }
    }

    @Test
    fun `when successful resolves with null`() {
        `when`(userClient.deregisterUser(any(), any())).thenAnswer {
            it.getArgument<OneginiDeregisterUserProfileHandler>(1).onSuccess()
        }

        DeregisterUserUseCase()("123456", promiseMock)

        argumentCaptor<UserProfile> {
            verify(userClient).deregisterUser(capture(), any())

            Assert.assertEquals("123456", firstValue.profileId)
        }

        verify(promiseMock).resolve(null)
    }
}
