package com.onegini.mobile.sdk.reactnative

import android.net.Uri
import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiBrowserRegistrationCallback
import com.onegini.mobile.sdk.reactnative.clean.use_cases.HandleRegistrationCallbackUseCase
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.reactnative.facade.UriFacade
import com.onegini.mobile.sdk.reactnative.handlers.registration.RegistrationEventEmitter
import com.onegini.mobile.sdk.reactnative.handlers.registration.RegistrationRequestHandler
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.spy
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)

class HandleRegistrationCallbackUseCaseTests {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private lateinit var oneginiSdk: OneginiSDK

    @Mock
    private lateinit var callbackMock: OneginiBrowserRegistrationCallback

    @Mock
    private lateinit var promiseMock: Promise

    @Mock
    private lateinit var uriMock: Uri

    @Mock
    private lateinit var parsedUri: Uri

    @Mock
    private lateinit var uriFacade: UriFacade

    private lateinit var handleRegistrationCallbackUseCase: HandleRegistrationCallbackUseCase

    @Mock
    private lateinit var registrationEventEmitter: RegistrationEventEmitter

    private lateinit var registrationRequestHandler: RegistrationRequestHandler

    val validUriString = "reactnativeexample://loginsuccess?code=D6E203A425AE4CA3C25AFC54AD520E2DF97A53133897F8CC3E54766BE7C107C1&state=IT1KDB2PAGEOAFBCCQJYU3VI"
    val invalidUri = "invalidUri"

    @Before
    fun setup() {
        registrationRequestHandler = RegistrationRequestHandler(registrationEventEmitter)
        mockRegistrationRequestHandler()
        handleRegistrationCallbackUseCase = HandleRegistrationCallbackUseCase(oneginiSdk, uriFacade)
    }

    @Test
    fun `should resolve when passing a uri and registration is in progress`() {
        whenStartedRegistration()
        handleRegistrationCallbackUseCase(validUriString, promiseMock)
        verify(promiseMock).resolve(null)
    }

    @Test
    fun `should call handleRegistrationCallback with parsed uri when registration is in progress`() {
        whenStartedRegistration()
        mockParseUri(validUriString)
        handleRegistrationCallbackUseCase(validUriString, promiseMock)
        verify(oneginiSdk.registrationRequestHandler).handleRegistrationCallback(parsedUri)
    }

    @Test
    fun `should reject when passing a uri and registration is not in progress`() {
        handleRegistrationCallbackUseCase(validUriString, promiseMock)
        verify(promiseMock).reject(OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.code, OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.message)
    }

    @Test
    fun `should pass an invalid uri into the sdk to get handled there when registration is in progress`() {
        mockParseUri(invalidUri)
        handleRegistrationCallbackUseCase(invalidUri, promiseMock)
        verify(oneginiSdk.registrationRequestHandler).handleRegistrationCallback(parsedUri)
    }

    private fun whenStartedRegistration() {
        oneginiSdk.registrationRequestHandler.startRegistration(uriMock, callbackMock)
    }

    private fun mockParseUri(uri: String) {
        whenever(uriFacade.parse(uri)).thenReturn(parsedUri)
    }

    private fun mockRegistrationRequestHandler() {
        val spyRegistrationRequestHandler = spy(registrationRequestHandler)
        `when`(oneginiSdk.registrationRequestHandler).thenReturn(spyRegistrationRequestHandler)
    }
}