package com.onegini.mobile.sdk.reactnative

import android.net.Uri
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.OneginiAppToWebSingleSignOnHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiAppToWebSingleSignOnError
import com.onegini.mobile.sdk.android.model.OneginiAppToWebSingleSignOn
import com.onegini.mobile.sdk.reactnative.clean.use_cases.StartSingleSignOnUseCase
import com.onegini.mobile.sdk.reactnative.facade.UriFacade
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)

class StartSingleSignOnUseCaseTests {
  @get:Rule
  val reactArgumentsTestRule = ReactArgumentsTestRule()

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private lateinit var oneginiSdk: OneginiSDK

  @Mock
  private lateinit var uriFacade: UriFacade

  // We need to deep stub here to mock a uri object's .toString() as we cant pass a uriFacade into the OneginiAppToWebSingleSignOn
  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private lateinit var oneginiAppToWebSingleSignOn: OneginiAppToWebSingleSignOn

  @Mock
  private lateinit var oneginiAppToWebSingleSignOnError: OneginiAppToWebSingleSignOnError

  private lateinit var startSingleSignOnUseCase: StartSingleSignOnUseCase

  @Mock
  private lateinit var promiseMock: Promise

  @Mock
  private lateinit var parsedUri: Uri

  private val correctUri = "https://login-mobile.test.onegini.com/personal/dashboard"
  private val mockedTokenString = "mockedToken"
  private val mockedRedirectUrlString = "mockedRedirectUrl"

  @Before
  fun setup() {
    startSingleSignOnUseCase = StartSingleSignOnUseCase(oneginiSdk, uriFacade)
  }

  @Test
  fun `When oginini getAppToWebSingleSignOn calls onSuccess on the handler, Then promise should resolve with a map containing the content from the result`() {
    mockParseUri(correctUri)
    mockSingleSignOnObject()
    whenever(oneginiSdk.oneginiClient.userClient.getAppToWebSingleSignOn(any(), any())).thenAnswer {
      it.getArgument<OneginiAppToWebSingleSignOnHandler>(1).onSuccess(oneginiAppToWebSingleSignOn)
    }
    startSingleSignOnUseCase(correctUri, promiseMock)
    argumentCaptor<JavaOnlyMap> {
      verify(promiseMock).resolve(capture())
      assertEquals(firstValue.getString("url"), mockedRedirectUrlString)
      assertEquals(firstValue.getString("token"), mockedTokenString)
    }
  }

  @Test
  fun `When oginini getAppToWebSingleSignOn calls onError on the handler, Then promise should reject with the error message and code`() {
    mockParseUri(correctUri)
    whenever(oneginiSdk.oneginiClient.userClient.getAppToWebSingleSignOn(any(), any())).thenAnswer {
      it.getArgument<OneginiAppToWebSingleSignOnHandler>(1).onError(oneginiAppToWebSingleSignOnError)
    }
    startSingleSignOnUseCase(correctUri, promiseMock)
    verify(promiseMock).reject(oneginiAppToWebSingleSignOnError.errorType.toString(), oneginiAppToWebSingleSignOnError.message)
  }

  private fun mockSingleSignOnObject() {
    `when`(oneginiAppToWebSingleSignOn.token).thenReturn(mockedTokenString)
    `when`(oneginiAppToWebSingleSignOn.redirectUrl.toString()).thenReturn(mockedRedirectUrlString)
  }

  private fun mockParseUri(uri: String) {
    whenever(uriFacade.parse(uri)).thenReturn(parsedUri)
  }
}
