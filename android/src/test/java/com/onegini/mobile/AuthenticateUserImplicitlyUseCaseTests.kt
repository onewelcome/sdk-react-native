package com.onegini.mobile

import com.onegini.mobile.clean.use_cases.AuthenticateUserImplicitlyUseCase
import com.onegini.mobile.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.android.handlers.OneginiImplicitAuthenticationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiImplicitTokenRequestError
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.lenient
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class AuthenticateUserImplicitlyUseCaseTests : BaseTests() {

    @Mock
    lateinit var authenticationError: OneginiImplicitTokenRequestError

    @Before
    fun prepareIdentityProviders() {
        lenient().`when`(authenticationError.errorType).thenReturn(666)
        lenient().`when`(authenticationError.message).thenReturn("MyError")

        lenient().`when`(userClient.userProfiles).thenReturn(setOf(UserProfile("123456")))
    }

    //

    @Test
    fun `when no profile is found rejects with error`() {
        AuthenticateUserImplicitlyUseCase()("444333", promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals(OneginiWrapperErrors.USER_PROFILE_IS_NULL.code, firstValue)
            Assert.assertEquals(OneginiWrapperErrors.USER_PROFILE_IS_NULL.message, secondValue)
        }
    }

    @Test
    fun `when successful should resolve with null`() {
        Mockito.`when`(userClient.authenticateUserImplicitly(any(), any(), any())).thenAnswer {
            it.getArgument<OneginiImplicitAuthenticationHandler>(2).onSuccess(UserProfile("666666"))
        }

        AuthenticateUserImplicitlyUseCase()("123456", promiseMock)

        verify(promiseMock).resolve(null)
    }

    @Test
    fun `when fails rejects with proper error`() {
        Mockito.`when`(userClient.authenticateUserImplicitly(any(), any(), any())).thenAnswer {
            it.getArgument<OneginiImplicitAuthenticationHandler>(2).onError(authenticationError)
        }

        AuthenticateUserImplicitlyUseCase()("123456", promiseMock)

        argumentCaptor<UserProfile> {
            verify(userClient).authenticateUserImplicitly(capture(), any(), any())

            Assert.assertEquals("123456", firstValue.profileId)
        }

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals("666", firstValue)
            Assert.assertEquals("MyError", secondValue)
        }
    }
}
