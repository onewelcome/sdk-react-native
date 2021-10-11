package com.onegini.mobile

import com.facebook.react.bridge.Promise
import com.onegini.mobile.clean.use_cases.EnrollMobileAuthenticationUseCase
import com.onegini.mobile.sdk.android.handlers.OneginiMobileAuthEnrollmentHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiMobileAuthEnrollmentError
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class EnrollMobileAuthenticationUseCaseTests {

    @get:Rule
    val reactArgumentsTestRule = ReactArgumentsTestRule()

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    @Mock
    lateinit var enrollError: OneginiMobileAuthEnrollmentError

    @Test
    fun `when success should resolve with null`() {
        Mockito.`when`(oneginiSdk.oneginiClient.userClient.enrollUserForMobileAuth(any())).thenAnswer {
            it.getArgument<OneginiMobileAuthEnrollmentHandler>(0).onSuccess()
        }

        EnrollMobileAuthenticationUseCase(oneginiSdk)(promiseMock)

        verify(oneginiSdk.oneginiClient.userClient).enrollUserForMobileAuth(any())

        verify(promiseMock).resolve(null)
    }

    @Test
    fun `when fails rejects with proper error`() {
        whenEnrollMobileAuthenticationFailed()

        EnrollMobileAuthenticationUseCase(oneginiSdk)(promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals("666", firstValue)
            Assert.assertEquals("MyError", secondValue)
        }
    }

    private fun whenEnrollMobileAuthenticationFailed() {
        `when`(enrollError.errorType).thenReturn(666)
        `when`(enrollError.message).thenReturn("MyError")
        `when`(oneginiSdk.oneginiClient.userClient.enrollUserForMobileAuth(any())).thenAnswer {
            it.getArgument<OneginiMobileAuthEnrollmentHandler>(0).onError(enrollError)
        }
    }
}
