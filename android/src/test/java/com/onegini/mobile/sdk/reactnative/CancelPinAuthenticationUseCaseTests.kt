package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiPinCallback
import com.onegini.mobile.sdk.android.model.entity.AuthenticationAttemptCounter
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.clean.use_cases.CancelPinAuthenticationUseCase
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.AUTHENTICATION_NOT_IN_PROGRESS
import com.onegini.mobile.sdk.reactnative.handlers.pins.PinAuthenticationEventEmitter
import com.onegini.mobile.sdk.reactnative.handlers.pins.PinAuthenticationRequestHandler
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class CancelPinAuthenticationUseCaseTests {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    @Mock
    lateinit var oneginiPinCallback: OneginiPinCallback

    @Mock
    lateinit var pinAuthenticationEventEmitter: PinAuthenticationEventEmitter

    @Mock
    lateinit var authenticationAttemptCounter: AuthenticationAttemptCounter

    lateinit var cancelPinAuthenticationUseCase: CancelPinAuthenticationUseCase

    lateinit var pinAuthenticationRequestHandler: PinAuthenticationRequestHandler

    @Before
    fun setup() {
        pinAuthenticationRequestHandler = PinAuthenticationRequestHandler(pinAuthenticationEventEmitter)
        cancelPinAuthenticationUseCase = CancelPinAuthenticationUseCase(pinAuthenticationRequestHandler)
    }

    @Test
    fun `When pin authentication has not started should reject with AUTHENTICATION_NOT_IN_PROGRESS`() {
        cancelPinAuthenticationUseCase(promiseMock)
        verify(promiseMock).reject(AUTHENTICATION_NOT_IN_PROGRESS.code.toString(), AUTHENTICATION_NOT_IN_PROGRESS.message)
    }

    @Test
    fun `When pin authentication has started should resolve with null`() {
        whenPinAuthenticationStarted()
        cancelPinAuthenticationUseCase(promiseMock)
        verify(promiseMock).resolve(null)
    }

    private fun whenPinAuthenticationStarted() {
        pinAuthenticationRequestHandler.startAuthentication(UserProfile("123456"), oneginiPinCallback, authenticationAttemptCounter)
    }
}
