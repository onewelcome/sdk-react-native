package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.OneginiMobileAuthWithOtpHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiMobileAuthWithOtpError
import com.onegini.mobile.sdk.reactnative.clean.use_cases.HandleMobileAuthWithOtpUseCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)

class HandleMobileAuthWithOtpUseCaseTests {
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private lateinit var oneginiSdk: OneginiSDK

    @Mock
    private lateinit var promiseMock: Promise

    @Mock
    private lateinit var oneginiMobileAuthWithOtpError: OneginiMobileAuthWithOtpError

    private lateinit var handleMobileAuthWithOtpUseCase: HandleMobileAuthWithOtpUseCase
    @Before
    fun setup() {
        handleMobileAuthWithOtpUseCase = HandleMobileAuthWithOtpUseCase(oneginiSdk)
    }

    @Test
    fun `When oneginiSdk calls onSuccess on the handler then the promise should resolve`() {
        whenever(oneginiSdk.oneginiClient.userClient.handleMobileAuthWithOtp(any(), any())).thenAnswer {
            it.getArgument<OneginiMobileAuthWithOtpHandler>(1).onSuccess()
        }
        handleMobileAuthWithOtpUseCase("1234", promiseMock)
        verify(promiseMock).resolve(null)
    }

    @Test
    fun `When oneginiSdk calls onError on the handler then promise should reject with the error message and code`() {
        whenever(oneginiSdk.oneginiClient.userClient.handleMobileAuthWithOtp(any(), any())).thenAnswer {
            it.getArgument<OneginiMobileAuthWithOtpHandler>(1).onError(oneginiMobileAuthWithOtpError)
        }
        handleMobileAuthWithOtpUseCase("1234", promiseMock)
        verify(promiseMock).reject(oneginiMobileAuthWithOtpError.errorType.toString(), oneginiMobileAuthWithOtpError.message)
    }
}
