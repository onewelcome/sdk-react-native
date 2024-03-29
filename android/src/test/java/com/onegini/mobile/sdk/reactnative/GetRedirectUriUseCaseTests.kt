package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetRedirectUriUseCase
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class GetRedirectUriUseCaseTests {

  @get:Rule
  val reactArgumentsTestRule = ReactArgumentsTestRule()

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  lateinit var oneginiSdk: OneginiSDK

  @Mock
  lateinit var promiseMock: Promise

  @Test
  fun `When calling redirectUri, Then should return proper url`() {
    `when`(oneginiSdk.oneginiClient.configModel).thenReturn(TestData.configModel)

    GetRedirectUriUseCase(oneginiSdk)(promiseMock)
    verify(promiseMock).resolve(TestData.configModel.redirectUri)
  }
}
