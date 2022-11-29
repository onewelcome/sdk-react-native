package com.onegini.mobile.sdk.reactnative

import android.net.Uri
import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiBrowserRegistrationCallback
import com.onegini.mobile.sdk.reactnative.clean.use_cases.CancelCustomRegistrationUseCase
import com.onegini.mobile.sdk.reactnative.exception.CANCEL_CUSTOM_REGISTRATION_NOT_ALLOWED
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.ACTION_NOT_ALLOWED
import com.onegini.mobile.sdk.reactnative.handlers.customregistration.SimpleCustomRegistrationAction
import com.onegini.mobile.sdk.reactnative.handlers.customregistration.SimpleCustomRegistrationFactory
import com.onegini.mobile.sdk.reactnative.handlers.registration.RegistrationEventEmitter
import com.onegini.mobile.sdk.reactnative.handlers.registration.RegistrationRequestHandler
import com.onegini.mobile.sdk.reactnative.model.rn.ReactNativeIdentityProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class CancelCustomRegistrationUseCaseTests {

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

    lateinit var cancelCustomRegistrationUseCase: CancelCustomRegistrationUseCase

    lateinit var registrationRequestHandler: RegistrationRequestHandler

    lateinit var simpleCustomRegistrationFactory: SimpleCustomRegistrationFactory


    private val cancelMessage = "cancel message"

    @Before
    fun setup() {
        registrationRequestHandler = RegistrationRequestHandler(registrationEventEmitter)
        cancelCustomRegistrationUseCase = CancelCustomRegistrationUseCase(oneginiSdk)
    }

    @Test
    fun `When registration has not started should reject with ACTION_NOT_ALLOWED`() {
        cancelCustomRegistrationUseCase(cancelMessage, promiseMock)
        verify(promiseMock).reject(ACTION_NOT_ALLOWED.code.toString(), CANCEL_CUSTOM_REGISTRATION_NOT_ALLOWED)
    }

    @Test
    fun `When registration has started but pin creation has also started should reject with ACTION_NOT_ALLOWED`() {
        whenRegistrationStarted()
        whenPinCreationStarted()
        cancelCustomRegistrationUseCase(cancelMessage, promiseMock)
        verify(promiseMock).reject(ACTION_NOT_ALLOWED.code.toString(), CANCEL_CUSTOM_REGISTRATION_NOT_ALLOWED)
    }

    @Test
    fun `When registration has started and pin creation has not started should resolve with null`() {
        whenRegistrationStarted()
        cancelCustomRegistrationUseCase(cancelMessage, promiseMock)
        verify(promiseMock).resolve(null)
    }

    private fun whenRegistrationStarted() {
        registrationRequestHandler.startRegistration(uri, oneginiBrowserRegistrationCallback)
    }

    private fun whenPinCreationStarted() {
        registrationRequestHandler.handleRegistrationCallback(null)
    }


    private fun whenIdentityProviderExists(isTwoStep: Boolean) {
        val identityProvider = ReactNativeIdentityProvider(TestData.identityProvider1.id, isTwoStep)
        val customRegistrationAction = spy(simpleCustomRegistrationFactory.getSimpleCustomRegistrationProvider(identityProvider).action)
        val list = ArrayList<SimpleCustomRegistrationAction>()
        list.add(customRegistrationAction)
        `when`(oneginiSdk.simpleCustomRegistrationActions).thenReturn(list)
    }

    private fun whenRegistrationIsInProgress() {
        // finishRegistration is what is called by the SDK when it starts registration
        oneginiSdk.simpleCustomRegistrationActions.first().finishRegistration(oneginiCustomRegistrationCallback, null)
    }
}
