package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiCustomRegistrationCallback
import com.onegini.mobile.sdk.reactnative.clean.use_cases.SubmitCustomRegistrationActionUseCase
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.ACTION_NOT_ALLOWED
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.IDENTITY_PROVIDER_NOT_FOUND
import com.onegini.mobile.sdk.reactnative.exception.SUBMIT_CUSTOM_REGISTRATION_ACTION_NOT_ALLOWED
import com.onegini.mobile.sdk.reactnative.handlers.customregistration.CustomRegistrationEventEmitter
import com.onegini.mobile.sdk.reactnative.handlers.customregistration.TwoStepCustomRegistrationAction
import com.onegini.mobile.sdk.reactnative.managers.RegistrationActionManager
import com.onegini.mobile.sdk.reactnative.model.rn.ReactNativeIdentityProvider
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito.spy
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class SubmitCustomRegistrationActionUseCaseTests {

    @get:Rule
    val reactArgumentsTestRule = ReactArgumentsTestRule()

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    @Mock
    lateinit var customRegistrationEventEmitter: CustomRegistrationEventEmitter

    @Mock
    lateinit var oneginiCustomRegistrationCallback: OneginiCustomRegistrationCallback

    lateinit var submitCustomRegistrationActionUseCase: SubmitCustomRegistrationActionUseCase

    lateinit var registrationActionManager: RegistrationActionManager

    private val token = "testToken"

    @Before
    fun setup() {
        registrationActionManager = spy(RegistrationActionManager())
        submitCustomRegistrationActionUseCase = SubmitCustomRegistrationActionUseCase(registrationActionManager)
    }

    @Test
    fun `When identity provider does not exist, Then should reject with IDENTITY_PROVIDER_NOT_FOUND error`() {
        submitCustomRegistrationActionUseCase(TestData.identityProvider1.id, token, promiseMock)
        verify(promiseMock).reject(IDENTITY_PROVIDER_NOT_FOUND.code.toString(), IDENTITY_PROVIDER_NOT_FOUND.message)
    }

    @Test
    fun `When identity provider exists but registration is not in progress, Then should reject with ACTION_NOT_ALLOWED error`() {
        whenIdentityProviderExists(false)
        submitCustomRegistrationActionUseCase(TestData.identityProvider1.id, token, promiseMock)
        verify(promiseMock).reject(ACTION_NOT_ALLOWED.code.toString(), SUBMIT_CUSTOM_REGISTRATION_ACTION_NOT_ALLOWED)
    }

    @Test
    fun `OneStep - When identity provider exists and registration is in progress, Then should resolve with null`() {
        whenIdentityProviderExists(false)
        whenRegistrationIsInProgress()
        submitCustomRegistrationActionUseCase(TestData.identityProvider1.id, token, promiseMock)
        verify(promiseMock).resolve(null)
    }

    @Test
    fun `TwoStep - When identity provider exists and registration is in progress, Then should resolve with null`() {
        whenIdentityProviderExists(true)
        whenRegistrationIsInProgress()
        submitCustomRegistrationActionUseCase(TestData.identityProvider1.id, token, promiseMock)
        verify(promiseMock).resolve(null)
    }

    @Test
    fun `OneStep - When identity provider exists and registration is in progress, Then should call returnSuccess with the supplied token on the registration action`() {
        whenIdentityProviderExists(false)
        whenRegistrationIsInProgress()
        submitCustomRegistrationActionUseCase(TestData.identityProvider1.id, token, promiseMock)
        verify(registrationActionManager.getCustomRegistrationActions().first()).returnSuccess(token)
    }

    @Test
    fun `TwoStep - When identity provider exists and registration is in progress, Then should call returnSuccess with the supplied token on the registration action`() {
        whenIdentityProviderExists(true)
        whenRegistrationIsInProgress()
        submitCustomRegistrationActionUseCase(TestData.identityProvider1.id, token, promiseMock)
        verify(registrationActionManager.getCustomRegistrationActions().first()).returnSuccess(token)
    }

    private fun whenIdentityProviderExists(isTwoStep: Boolean) {
        val identityProvider = ReactNativeIdentityProvider(TestData.identityProvider1.id, isTwoStep)
        val customRegistrationAction = spy(TwoStepCustomRegistrationAction(identityProvider.id, customRegistrationEventEmitter))
        registrationActionManager.addCustomRegistrationAction(customRegistrationAction)
    }

    private fun whenRegistrationIsInProgress() {
        // finishRegistration is what is called by the SDK when it starts registration
        registrationActionManager.getCustomRegistrationActions().first().finishRegistration(oneginiCustomRegistrationCallback, null)
    }

}
