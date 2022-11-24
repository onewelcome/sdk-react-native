package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.OneginiMobileAuthEnrollmentHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiMobileAuthEnrollmentError
import com.onegini.mobile.sdk.reactnative.clean.use_cases.EnrollMobileAuthenticationUseCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class EnrollMobileAuthenticationUseCaseTests {

    @get:Rule
    val reactArgumentsTestRule = ReactArgumentsTestRule()

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    @Mock
    lateinit var oneginiMobileAuthEnrollmentError: OneginiMobileAuthEnrollmentError

    @Test
    fun `When android sdk calls onSuccess on the handler of enrollUserForMobileAuth, promise should resolve with null`() {
        whenever(oneginiSdk.oneginiClient.userClient.enrollUserForMobileAuth(any())).thenAnswer {
            it.getArgument<OneginiMobileAuthEnrollmentHandler>(0).onSuccess()
        }
        EnrollMobileAuthenticationUseCase(oneginiSdk)(promiseMock)
        verify(promiseMock).resolve(null)
    }

    @Test
    fun `When android sdk calls onError on the handler of enrollUserForMobileAuth, promise should reject with the given code and message`() {
        whenever(oneginiSdk.oneginiClient.userClient.enrollUserForMobileAuth(any())).thenAnswer {
            it.getArgument<OneginiMobileAuthEnrollmentHandler>(0).onError(oneginiMobileAuthEnrollmentError)
        }
        EnrollMobileAuthenticationUseCase(oneginiSdk)(promiseMock)
        verify(promiseMock).reject(oneginiMobileAuthEnrollmentError.errorType.toString(), oneginiMobileAuthEnrollmentError.message)
    }

}
