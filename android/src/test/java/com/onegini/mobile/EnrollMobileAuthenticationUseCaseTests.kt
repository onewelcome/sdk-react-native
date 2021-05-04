package com.onegini.mobile

import com.onegini.mobile.clean.use_cases.EnrollMobileAuthenticationUseCase
import com.onegini.mobile.sdk.android.handlers.OneginiMobileAuthEnrollmentHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiMobileAuthEnrollmentError
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class EnrollMobileAuthenticationUseCaseTests : BaseTests() {

    @Mock
    lateinit var enrollError: OneginiMobileAuthEnrollmentError

    @Before
    fun prepareIdentityProviders() {
        Mockito.lenient().`when`(enrollError.errorType).thenReturn(666)
        Mockito.lenient().`when`(enrollError.message).thenReturn("MyError")
    }

    //

    @Test
    fun `when success should resolve with null`() {
        Mockito.`when`(userClient.enrollUserForMobileAuth(any())).thenAnswer {
            it.getArgument<OneginiMobileAuthEnrollmentHandler>(0).onSuccess()
        }

        EnrollMobileAuthenticationUseCase()(promiseMock)

        verify(userClient).enrollUserForMobileAuth(any())

        verify(promiseMock).resolve(null)
    }

    @Test
    fun `when fails rejects with proper error`() {
        Mockito.`when`(userClient.enrollUserForMobileAuth(any())).thenAnswer {
            it.getArgument<OneginiMobileAuthEnrollmentHandler>(0).onError(enrollError)
        }

        EnrollMobileAuthenticationUseCase()(promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals("666", firstValue)
            Assert.assertEquals("MyError", secondValue)
        }
    }
}
