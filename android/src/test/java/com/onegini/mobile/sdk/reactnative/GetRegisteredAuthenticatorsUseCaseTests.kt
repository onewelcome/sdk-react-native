package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetRegisteredAuthenticatorsUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetUserProfileUseCase
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError
import org.junit.Assert.*
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

@RunWith(MockitoJUnitRunner::class)
class GetRegisteredAuthenticatorsUseCaseTests {

  @get:Rule
  val reactArgumentsTestRule = ReactArgumentsTestRule()

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  lateinit var oneginiSdk: OneginiSDK

  @Mock
  lateinit var promiseMock: Promise

  @Mock
  lateinit var getUserProfileUseCase: GetUserProfileUseCase

  private lateinit var getRegisteredAuthenticatorsUseCase: GetRegisteredAuthenticatorsUseCase

  @Before
  fun setup() {
    getRegisteredAuthenticatorsUseCase = GetRegisteredAuthenticatorsUseCase(oneginiSdk, getUserProfileUseCase)
  }

  @Test
  fun `when no profile is found should reject with error`() {
    `when`(oneginiSdk.oneginiClient.userClient.getRegisteredAuthenticators(any())).thenReturn(
      setOf(
        TestData.authenticator1,
        TestData.authenticator2
      )
    )

    getRegisteredAuthenticatorsUseCase("123456", promiseMock)
    verify(promiseMock).reject(
      OneginiWrapperError.PROFILE_DOES_NOT_EXIST.code.toString(),
      OneginiWrapperError.PROFILE_DOES_NOT_EXIST.message
    )
  }

  @Test
  fun `should resolve with list of authenticators for specific user profile`() {
    `when`(oneginiSdk.oneginiClient.userClient.getRegisteredAuthenticators(any())).thenReturn(
      setOf(
        TestData.authenticator1,
        TestData.authenticator2
      )
    )

    `when`(getUserProfileUseCase.invoke(any())).thenReturn(UserProfile("123456"))

    getRegisteredAuthenticatorsUseCase("123456", promiseMock)

    argumentCaptor<JavaOnlyArray> {
      verify(promiseMock).resolve(capture())

      assertEquals(2, firstValue.size())

      assertEquals(TestData.authenticator1.id, firstValue.getMap(0).getString("id"))
      assertEquals(TestData.authenticator1.name, firstValue.getMap(0).getString("name"))
      assertEquals(TestData.authenticator1.type, firstValue.getMap(0).getInt("type"))
      assertEquals(TestData.authenticator1.isPreferred, firstValue.getMap(0).getBoolean("isPreferred"))
      assertEquals(TestData.authenticator1.isRegistered, firstValue.getMap(0).getBoolean("isRegistered"))
      assertEquals(TestData.authenticator1.userProfile.profileId, firstValue.getMap(0).getMap("userProfile")?.getString("id"))

      assertEquals(TestData.authenticator2.id, firstValue.getMap(1).getString("id"))
      assertEquals(TestData.authenticator2.name, firstValue.getMap(1).getString("name"))
      assertEquals(TestData.authenticator2.type, firstValue.getMap(1).getInt("type"))
      assertEquals(TestData.authenticator2.isPreferred, firstValue.getMap(1).getBoolean("isPreferred"))
      assertEquals(TestData.authenticator2.isRegistered, firstValue.getMap(1).getBoolean("isRegistered"))
      assertEquals(TestData.authenticator2.userProfile.profileId, firstValue.getMap(1).getMap("userProfile")?.getString("id"))
    }
  }
}
