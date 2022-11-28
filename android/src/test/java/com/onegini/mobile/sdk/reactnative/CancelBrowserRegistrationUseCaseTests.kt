package com.onegini.mobile.sdk.reactnative

import android.net.Uri
import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiBrowserRegistrationCallback
import com.onegini.mobile.sdk.reactnative.clean.use_cases.CancelBrowserRegistrationUseCase
import com.onegini.mobile.sdk.reactnative.exception.CANCEL_BROWSER_REGISTRATION_NOT_ALLOWED
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.ACTION_NOT_ALLOWED
import com.onegini.mobile.sdk.reactnative.handlers.registration.RegistrationEventEmitter
import com.onegini.mobile.sdk.reactnative.handlers.registration.RegistrationRequestHandler
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class CancelBrowserRegistrationUseCaseTests {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    @Mock
    lateinit var uri: Uri

    @Mock
    lateinit var registrationEventEmitter: RegistrationEventEmitter

    @Mock
    lateinit var oneginiBrowserRegistrationCallback: OneginiBrowserRegistrationCallback

    lateinit var cancelBrowserRegistrationUseCase: CancelBrowserRegistrationUseCase

    lateinit var registrationRequestHandler: RegistrationRequestHandler

    @Before
    fun setup() {
        registrationRequestHandler = RegistrationRequestHandler(registrationEventEmitter)
        cancelBrowserRegistrationUseCase = CancelBrowserRegistrationUseCase(registrationRequestHandler)
    }

    @Test
    fun `When registration has not started should reject with ACTION_NOT_ALLOWED`() {
        cancelBrowserRegistrationUseCase(promiseMock)
        verify(promiseMock).reject(ACTION_NOT_ALLOWED.code.toString(), CANCEL_BROWSER_REGISTRATION_NOT_ALLOWED)
    }

    @Test
    fun `When registration has started but pin creation has also started should reject with ACTION_NOT_ALLOWED`() {
        whenRegistrationStarted()
        whenPinCreationStarted()
        cancelBrowserRegistrationUseCase(promiseMock)
        verify(promiseMock).reject(ACTION_NOT_ALLOWED.code.toString(), CANCEL_BROWSER_REGISTRATION_NOT_ALLOWED)
    }

    @Test
    fun `When registration has started and pin creation has not started should resolve with null`() {
        whenRegistrationStarted()
        cancelBrowserRegistrationUseCase(promiseMock)
        verify(promiseMock).resolve(null)
    }

    private fun whenRegistrationStarted() {
        registrationRequestHandler.startRegistration(uri, oneginiBrowserRegistrationCallback)
    }

    private fun whenPinCreationStarted() {
        registrationRequestHandler.handleRegistrationCallback(null)
    }
}
