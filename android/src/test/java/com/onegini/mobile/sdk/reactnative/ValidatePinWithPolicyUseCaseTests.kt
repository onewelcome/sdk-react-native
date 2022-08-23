package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.OneginiPinValidationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiPinValidationError
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.clean.use_cases.ValidatePinWithPolicyUseCase
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.eq
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class ValidatePinWithPolicyUseCaseTests {

    @get:Rule
    val reactArgumentsTestRule = ReactArgumentsTestRule()

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    @Mock
    lateinit var oneginiPinValidationErrorMock: OneginiPinValidationError

    @Test
    fun `When supplying null as pin should reject with PARAMETERS_NOT_CORRECT error and not resolve`() {
        val pin = null
        whenever(oneginiSdk.oneginiClient.userClient.authenticatedUserProfile).thenReturn(UserProfile("testId"))
        ValidatePinWithPolicyUseCase(oneginiSdk)(pin, promiseMock)
        verify(promiseMock, never()).resolve(anyOrNull())
        verify(promiseMock).reject(OneginiWrapperErrors.PARAMETERS_NOT_CORRECT.code, "Expected parameter 'pin' to be String but was NULL")
    }

    @Test
    fun `When pin is not null should call validatePinWithPolicy on the onegini sdk`() {
        val pin = "14789"
        ValidatePinWithPolicyUseCase(oneginiSdk)(pin, promiseMock)

        verify(oneginiSdk.oneginiClient.userClient).validatePinWithPolicy(eq("14789".toCharArray()), any())
    }

    @Test
    fun `When oginini validatePinWithPolicy calls onSuccess on the handler, promise should resolve with null`() {
        val pin = "14789"
        whenever(oneginiSdk.oneginiClient.userClient.validatePinWithPolicy(any(), any())).thenAnswer {
            it.getArgument<OneginiPinValidationHandler>(1).onSuccess()
        }
        ValidatePinWithPolicyUseCase(oneginiSdk)(pin, promiseMock)

        verify(promiseMock).resolve(null)
    }

    private fun whenPinValidationReturnedError(errorCode: Int, errorMessage: String) {
        whenever(oneginiPinValidationErrorMock.errorType).thenReturn(errorCode)
        whenever(oneginiPinValidationErrorMock.message).thenReturn(errorMessage)
        whenever(oneginiSdk.oneginiClient.userClient.validatePinWithPolicy(any(), any())).thenAnswer {
            it.getArgument<OneginiPinValidationHandler>(1).onError(oneginiPinValidationErrorMock)
        }
    }
    @Test
    fun `When oginini validatePinWithPolicy calls onError on the handler, promise should reject with error from native sdk`() {
        val pin = "14789"
        val errorCode = 111
        val errorMessage = "message"
        whenPinValidationReturnedError(errorCode, errorMessage)

        ValidatePinWithPolicyUseCase(oneginiSdk)(pin, promiseMock)

        verify(promiseMock).reject(errorCode.toString(), errorMessage)
    }
}
