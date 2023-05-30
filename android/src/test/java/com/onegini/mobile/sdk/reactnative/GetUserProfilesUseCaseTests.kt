package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetUserProfilesUseCase
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class GetUserProfilesUseCaseTests {

  @get:Rule
  val reactArgumentsTestRule = ReactArgumentsTestRule()

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  lateinit var oneginiSdk: OneginiSDK

  @Mock
  lateinit var promiseMock: Promise

  @Test
  fun `should return parsed profiles`() {
    `when`(oneginiSdk.oneginiClient.userClient.userProfiles).thenReturn(setOf(UserProfile("123456"), UserProfile("234567")))

    GetUserProfilesUseCase(oneginiSdk)(promiseMock)

    argumentCaptor<JavaOnlyArray> {
      verify(promiseMock).resolve(capture())

      Assert.assertEquals(2, firstValue.size())
      Assert.assertEquals("123456", firstValue.getMap(0).getString("id"))
      Assert.assertEquals("234567", firstValue.getMap(1).getString("id"))
    }
  }
}
