package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.OneginiChangePinHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiChangePinError
import com.onegini.mobile.sdk.reactnative.clean.use_cases.ChangePinUseCase
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
class ChangePinUseCaseTests {

    @get:Rule
    val reactArgumentsTestRule = ReactArgumentsTestRule()

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    @Mock
    lateinit var oneginiChangePinError: OneginiChangePinError

    @Test
    fun `When android sdk calls onSuccess on the handler of changePin, promise should resolve with null`() {
        whenever(oneginiSdk.oneginiClient.userClient.changePin(any())).thenAnswer {
            it.getArgument<OneginiChangePinHandler>(0).onSuccess()
        }
        ChangePinUseCase(oneginiSdk)(promiseMock)
        verify(promiseMock).resolve(null)
    }

    @Test
    fun `When android sdk calls onError on the handler of changePin, promise should reject with the given code and message`() {
        whenever(oneginiSdk.oneginiClient.userClient.changePin(any())).thenAnswer {
            it.getArgument<OneginiChangePinHandler>(0).onError(oneginiChangePinError)
        }
        ChangePinUseCase(oneginiSdk)(promiseMock)
        verify(promiseMock).reject(oneginiChangePinError.errorType.toString(), oneginiChangePinError.message)
    }

}
