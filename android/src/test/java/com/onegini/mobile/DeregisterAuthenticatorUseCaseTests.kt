package com.onegini.mobile

import com.onegini.mobile.clean.use_cases.DeregisterAuthenticatorUseCase
import com.onegini.mobile.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticatorDeregistrationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiAuthenticatorDeregistrationError
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.lenient
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class DeregisterAuthenticatorUseCaseTests : BaseTests() {

    @Mock
    lateinit var deregistrationError: OneginiAuthenticatorDeregistrationError

    @Before
    fun prepareIdentityProviders() {
        lenient().`when`(deregistrationError.errorType).thenReturn(666)
        lenient().`when`(deregistrationError.message).thenReturn("MyError")

        lenient().`when`(userClient.userProfiles).thenReturn(setOf(UserProfile("123456")))
    }

    //

    @Test
    fun `when no profile is found rejects with error`() {
        DeregisterAuthenticatorUseCase()("444333", "Fingerprint", promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals(OneginiWrapperErrors.USER_PROFILE_IS_NULL.code, firstValue)
            Assert.assertEquals(OneginiWrapperErrors.USER_PROFILE_IS_NULL.message, secondValue)
        }
    }

    @Test
    fun `when no unregistered authenticator of provided type is found reject with error`() {
        lenient().`when`(userClient.getNotRegisteredAuthenticators(any())).thenReturn(setOf(TestData.authenticator1, TestData.authenticator2))

        DeregisterAuthenticatorUseCase()("123456", "Fingerprint", promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals(OneginiWrapperErrors.AUTHENTICATED_USER_PROFILE_IS_NULL.code, firstValue)
            Assert.assertEquals(OneginiWrapperErrors.AUTHENTICATED_USER_PROFILE_IS_NULL.message, secondValue)
        }
    }

    @Test
    fun `when successful should resolve with null`() {
        lenient().`when`(userClient.getRegisteredAuthenticators(any())).thenReturn(setOf(TestData.authenticator1, TestData.authenticator2, TestData.fingerprintAuthenticator))
        lenient().`when`(userClient.deregisterAuthenticator(any(), any())).thenAnswer {
            it.getArgument<OneginiAuthenticatorDeregistrationHandler>(1).onSuccess()
        }

        DeregisterAuthenticatorUseCase()("123456", "Fingerprint", promiseMock)

        verify(promiseMock).resolve(null)
    }

    @Test
    fun `when fails rejects with proper error`() {
        lenient().`when`(userClient.getRegisteredAuthenticators(any())).thenReturn(setOf(TestData.authenticator1, TestData.authenticator2, TestData.fingerprintAuthenticator))
        lenient().`when`(userClient.deregisterAuthenticator(any(), any())).thenAnswer {
            it.getArgument<OneginiAuthenticatorDeregistrationHandler>(1).onError(deregistrationError)
        }

        DeregisterAuthenticatorUseCase()("123456", "Fingerprint", promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals("666", firstValue)
            Assert.assertEquals("MyError", secondValue)
        }
    }
}
