package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiPinCallback
import com.onegini.mobile.sdk.android.model.entity.AuthenticationAttemptCounter
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.clean.use_cases.SubmitPinUseCase
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError.AUTHENTICATION_NOT_IN_PROGRESS
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError.INCORRECT_PIN_FLOW
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError.REGISTRATION_NOT_IN_PROGRESS
import com.onegini.mobile.sdk.reactnative.handlers.pins.CreatePinEventEmitter
import com.onegini.mobile.sdk.reactnative.handlers.pins.CreatePinRequestHandler
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
class SubmitPinUseCaseTests {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    @Mock
    lateinit var createPinEventEmitter: CreatePinEventEmitter

    @Mock
    lateinit var pinAuthenticationEventEmitter: PinAuthenticationEventEmitter

    @Mock
    lateinit var oneginiPinCallback: OneginiPinCallback

    @Mock
    lateinit var authenticationAttemptCounter: AuthenticationAttemptCounter

    lateinit var createPinRequestHandler: CreatePinRequestHandler
    lateinit var pinAuthenticationRequestHandler: PinAuthenticationRequestHandler
    lateinit var submitPinUseCase: SubmitPinUseCase

    private val pin = "14789"

    @Before
    fun setup() {
        createPinRequestHandler = CreatePinRequestHandler(createPinEventEmitter)
        pinAuthenticationRequestHandler = PinAuthenticationRequestHandler(pinAuthenticationEventEmitter)
        submitPinUseCase = SubmitPinUseCase(createPinRequestHandler, pinAuthenticationRequestHandler)
    }

    @Test
    fun `When called with an incorrect PinFlow, Then it should reject with INCORRECT_PIN_FLOW`() {
        submitPinUseCase("Not existing pinFlow", pin, promiseMock)
        verify(promiseMock).reject(INCORRECT_PIN_FLOW.code.toString(), INCORRECT_PIN_FLOW.message)
    }

    // Pin Create
    @Test
    fun `When called with create PinFlow without pin creation in progress, Then it should reject with REGISTRATION_NOT_IN_PROGRESS`() {
        submitPinUseCase(Constants.PinFlow.Create.toString(), pin, promiseMock)
        verify(promiseMock).reject(REGISTRATION_NOT_IN_PROGRESS.code.toString(), REGISTRATION_NOT_IN_PROGRESS.message)
    }

    @Test
    fun `When called with create PinFlow with pin creation in progress, Then it should resolve with null`() {
        whenPinCreationStarted()
        submitPinUseCase(Constants.PinFlow.Create.toString(), pin, promiseMock)
        verify(promiseMock).resolve(null)
    }

    @Test
    fun `When called with create PinFlow with pin creation in progress, Then it should call acceptAuthenticationRequest on the oneginiPinCallback with the given pin`() {
        whenPinCreationStarted()
        submitPinUseCase(Constants.PinFlow.Create.toString(), pin, promiseMock)
        verify(oneginiPinCallback).acceptAuthenticationRequest(pin.toCharArray())
    }

    // Pin Authentication
    @Test
    fun `When called with authentication PinFlow without authentication in progress, Then it should reject with AUTHENTICATION_NOT_IN_PROGRESS`() {
        submitPinUseCase(Constants.PinFlow.Authentication.toString(), pin, promiseMock)
        verify(promiseMock).reject(AUTHENTICATION_NOT_IN_PROGRESS.code.toString(), AUTHENTICATION_NOT_IN_PROGRESS.message)
    }

    @Test
    fun `When called with authentication PinFlow with pin authentication in progress, Then it should resolve with null`() {
        whenPinAuthenticationStarted()
        submitPinUseCase(Constants.PinFlow.Authentication.toString(), pin, promiseMock)
        verify(promiseMock).resolve(null)
    }

    @Test
    fun `When called with authentication PinFlow with pin authentication in progress, Then it should call acceptAuthenticationRequest on the oneginiPinCallback with the given pin`() {
        whenPinAuthenticationStarted()
        submitPinUseCase(Constants.PinFlow.Authentication.toString(), pin, promiseMock)
        verify(oneginiPinCallback).acceptAuthenticationRequest(pin.toCharArray())
    }

    private fun whenPinAuthenticationStarted() {
        // Since we Mock the SDK we need to call the startPinAuthentication ourselves on the CreatePinRequestHandler
        pinAuthenticationRequestHandler.startAuthentication(UserProfile("123456"), oneginiPinCallback, authenticationAttemptCounter)
    }

    private fun whenPinCreationStarted() {
        // Since we Mock the SDK we need to call the startPinCreation ourselves on the CreatePinRequestHandler
        createPinRequestHandler.startPinCreation(UserProfile("123456"), oneginiPinCallback, 5)
    }
}
