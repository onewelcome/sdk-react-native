package com.onegini.mobile.sdk.reactnative

import android.net.Uri
import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiBrowserRegistrationCallback
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiPinCallback
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.clean.use_cases.CancelPinCreationUseCase
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError.PIN_CREATION_NOT_IN_PROGRESS
import com.onegini.mobile.sdk.reactnative.handlers.pins.CreatePinEventEmitter
import com.onegini.mobile.sdk.reactnative.handlers.pins.CreatePinRequestHandler
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
class CancelPinCreationUseCaseTests {

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  lateinit var oneginiSdk: OneginiSDK

  @Mock
  lateinit var promiseMock: Promise

  @Mock
  lateinit var uri: Uri

  @Mock
  lateinit var registrationEventEmitter: RegistrationEventEmitter

  @Mock
  lateinit var createPinEventEmitter: CreatePinEventEmitter

  @Mock
  lateinit var oneginiBrowserRegistrationCallback: OneginiBrowserRegistrationCallback

  @Mock
  lateinit var oneginiPinCallback: OneginiPinCallback

  lateinit var createPinRequestHandler: CreatePinRequestHandler

  lateinit var cancelPinCreationUseCase: CancelPinCreationUseCase

  lateinit var registrationRequestHandler: RegistrationRequestHandler

  @Before
  fun setup() {
    registrationRequestHandler = RegistrationRequestHandler(registrationEventEmitter)
    createPinRequestHandler = CreatePinRequestHandler(createPinEventEmitter)
    cancelPinCreationUseCase = CancelPinCreationUseCase(createPinRequestHandler)
  }

  @Test
  fun `When pin creation has not started, Then should reject with PIN_CREATION_NOT_IN_PROGRESS`() {
    cancelPinCreationUseCase(promiseMock)
    verify(promiseMock).reject(PIN_CREATION_NOT_IN_PROGRESS.code.toString(), PIN_CREATION_NOT_IN_PROGRESS.message)
  }

  @Test
  fun `When registration has started and pin creation has not started, Then should reject with PIN_CREATION_NOT_IN_PROGRESS`() {
    whenRegistrationStarted()
    cancelPinCreationUseCase(promiseMock)
    verify(promiseMock).reject(PIN_CREATION_NOT_IN_PROGRESS.code.toString(), PIN_CREATION_NOT_IN_PROGRESS.message)
  }

  @Test
  fun `When pin creation has started, Then should resolve with null`() {
    whenRegistrationStarted()
    whenPinCreationStarted()
    cancelPinCreationUseCase(promiseMock)
    verify(promiseMock).resolve(null)
  }

  private fun whenRegistrationStarted() {
    registrationRequestHandler.startRegistration(uri, oneginiBrowserRegistrationCallback)
  }

  private fun whenPinCreationStarted() {
    registrationRequestHandler.handleRegistrationCallback(null)
    // Since we Mock the SDK we need to call the startPinCreation ourselves on the CreatePinRequestHandler
    createPinRequestHandler.startPinCreation(UserProfile("123456"), oneginiPinCallback, 5)
  }
}
