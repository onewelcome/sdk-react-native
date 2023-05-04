package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.OneginiInitializationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiInitializationError
import com.onegini.mobile.sdk.reactnative.clean.use_cases.StartClientUseCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class StartClientUseCaseTests {

  @get:Rule
  val reactArgumentsTestRule = ReactArgumentsTestRule()

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  lateinit var oneginiSdk: OneginiSDK

  @Mock
  lateinit var promiseMock: Promise

  @Mock
  lateinit var initalizationError: OneginiInitializationError


  @Test
  fun `When starting of the android native sdk is successfull, Then the promise should resolve with null`() {
    `when`(oneginiSdk.oneginiClient.start(any())).thenAnswer {
      it.getArgument<OneginiInitializationHandler>(0).onSuccess(emptySet())
    }
    StartClientUseCase(oneginiSdk)(promiseMock)
    verify(promiseMock).resolve(null)
  }

  @Test
  fun `when oneginiClient_start fails should reject and pass proper errors`() {
    `when`(oneginiSdk.oneginiClient.start(any())).thenAnswer {
      it.getArgument<OneginiInitializationHandler>(0).onError(initalizationError)
    }

    StartClientUseCase(oneginiSdk)(promiseMock)
    verify(promiseMock).reject(initalizationError.errorType.toString(), initalizationError.message)
  }
}
