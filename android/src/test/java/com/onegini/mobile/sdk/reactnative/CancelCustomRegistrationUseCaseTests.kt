package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiCustomRegistrationCallback
import com.onegini.mobile.sdk.reactnative.clean.use_cases.CancelCustomRegistrationUseCase
import com.onegini.mobile.sdk.reactnative.exception.CANCEL_CUSTOM_REGISTRATION_NOT_ALLOWED
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError.ACTION_NOT_ALLOWED
import com.onegini.mobile.sdk.reactnative.handlers.customregistration.CustomRegistrationEventEmitter
import com.onegini.mobile.sdk.reactnative.handlers.customregistration.TwoStepCustomRegistrationAction
import com.onegini.mobile.sdk.reactnative.handlers.registration.RegistrationEventEmitter
import com.onegini.mobile.sdk.reactnative.handlers.registration.RegistrationRequestHandler
import com.onegini.mobile.sdk.reactnative.managers.CustomRegistrationActionManager
import com.onegini.mobile.sdk.reactnative.model.rn.ReactNativeIdentityProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class CancelCustomRegistrationUseCaseTests {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  lateinit var oneginiSdk: OneginiSDK

  @Mock
  lateinit var promiseMock: Promise

  @Mock
  lateinit var registrationEventEmitter: RegistrationEventEmitter

  @Mock
  lateinit var customRegistrationEventEmitter: CustomRegistrationEventEmitter

  @Mock
  lateinit var oneginiCustomRegistrationCallback: OneginiCustomRegistrationCallback

  lateinit var cancelCustomRegistrationUseCase: CancelCustomRegistrationUseCase

  lateinit var registrationRequestHandler: RegistrationRequestHandler

  lateinit var customRegistrationActionManager: CustomRegistrationActionManager


  private val cancelMessage = "cancel message"
  private val successMessage = "success message"

  @Before
  fun setup() {
    customRegistrationActionManager = spy(CustomRegistrationActionManager())
    registrationRequestHandler = RegistrationRequestHandler(registrationEventEmitter)
    cancelCustomRegistrationUseCase = CancelCustomRegistrationUseCase(customRegistrationActionManager)
  }

  @Test
  fun `When registration has not started, Then should reject with ACTION_NOT_ALLOWED`() {
    cancelCustomRegistrationUseCase(cancelMessage, promiseMock)
    verify(promiseMock).reject(ACTION_NOT_ALLOWED.code.toString(), CANCEL_CUSTOM_REGISTRATION_NOT_ALLOWED)
  }

  // ONE STEP
  @Test
  fun `One Step - When registration has started but pin creation has also started, Then should reject with ACTION_NOT_ALLOWED`() {
    whenRegistrationStarted(false)
    whenPinCreationStarted()
    cancelCustomRegistrationUseCase(cancelMessage, promiseMock)
    verify(promiseMock).reject(ACTION_NOT_ALLOWED.code.toString(), CANCEL_CUSTOM_REGISTRATION_NOT_ALLOWED)
  }

  @Test
  fun `One Step - When registration has started and pin creation has not started, Then should resolve with null`() {
    whenRegistrationStarted(false)
    cancelCustomRegistrationUseCase(cancelMessage, promiseMock)
    verify(promiseMock).resolve(null)
  }

  @Test
  fun `One Step - When registration has started and pin creation has not started, Then should call returnError on the action`() {
    whenRegistrationStarted(false)
    cancelCustomRegistrationUseCase(cancelMessage, promiseMock)
    verify(customRegistrationActionManager.getCustomRegistrationActions().first()).returnError(any())
  }

  // TWO STEP
  @Test
  fun `Two Step - When registration has started but pin creation has also started, Then should reject with ACTION_NOT_ALLOWED`() {
    whenRegistrationStarted(true)
    whenPinCreationStarted()
    cancelCustomRegistrationUseCase(cancelMessage, promiseMock)
    verify(promiseMock).reject(ACTION_NOT_ALLOWED.code.toString(), CANCEL_CUSTOM_REGISTRATION_NOT_ALLOWED)
  }

  @Test
  fun `Two Step - When registration has started and pin creation has not started, Then should resolve with null`() {
    whenRegistrationStarted(true)
    cancelCustomRegistrationUseCase(cancelMessage, promiseMock)
    verify(promiseMock).resolve(null)
  }

  @Test
  fun `Two Step - When registration has started and pin creation has not started, Then should call returnError on the action`() {
    whenRegistrationStarted(true)
    cancelCustomRegistrationUseCase(cancelMessage, promiseMock)
    verify(customRegistrationActionManager.getCustomRegistrationActions().first()).returnError(any())
  }

  private fun whenRegistrationStarted(isTwoStep: Boolean) {
    // finishRegistration is what is called by the SDK when it starts registration
    whenIdentityProviderExists(isTwoStep)
    customRegistrationActionManager.getCustomRegistrationActions().first().finishRegistration(oneginiCustomRegistrationCallback, null)
  }

  private fun whenPinCreationStarted() {
    customRegistrationActionManager.getCustomRegistrationActions().first().returnSuccess(successMessage)
  }

  private fun whenIdentityProviderExists(isTwoStep: Boolean) {
    val identityProvider = ReactNativeIdentityProvider(TestData.identityProvider1.id, isTwoStep)
    val customRegistrationAction = spy(TwoStepCustomRegistrationAction(identityProvider.id, customRegistrationEventEmitter))
    customRegistrationActionManager.addCustomRegistrationAction(customRegistrationAction)
  }
}
