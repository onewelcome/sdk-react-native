package com.onegini.mobile

import com.facebook.react.bridge.Promise
import com.onegini.mobile.clean.use_cases.HandleMobileAuthWithOtpUseCase
import com.onegini.mobile.sdk.android.handlers.OneginiMobileAuthWithOtpHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiMobileAuthWithOtpError
import org.junit.Assert
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
class HandleMobileAuthWithOtpUseCaseTests {

    @get:Rule
    val reactArgumentsTestRule = ReactArgumentsTestRule()

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    @Mock
    lateinit var mobileAuthOtpError: OneginiMobileAuthWithOtpError

    @Test
    fun `when successful should resolve with null`() {
        `when`(oneginiSdk.oneginiClient.userClient.handleMobileAuthWithOtp(any(), any())).thenAnswer {
            it.getArgument<OneginiMobileAuthWithOtpHandler>(1).onSuccess()
        }

        HandleMobileAuthWithOtpUseCase(oneginiSdk)("code123", promiseMock)

        verify(promiseMock).resolve(null)
    }

    @Test
    fun `when fails should reject with proper error`() {
        whenHandleMobileAuthWithOtpFailed()

        HandleMobileAuthWithOtpUseCase(oneginiSdk)("code123", promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals("666", firstValue)
            Assert.assertEquals("MyError", secondValue)
        }
    }

    private fun whenHandleMobileAuthWithOtpFailed() {
        `when`(mobileAuthOtpError.errorType).thenReturn(666)
        `when`(mobileAuthOtpError.message).thenReturn("MyError")
        `when`(oneginiSdk.oneginiClient.userClient.handleMobileAuthWithOtp(any(), any())).thenAnswer {
            it.getArgument<OneginiMobileAuthWithOtpHandler>(1).onError(mobileAuthOtpError)
        }
    }
}
