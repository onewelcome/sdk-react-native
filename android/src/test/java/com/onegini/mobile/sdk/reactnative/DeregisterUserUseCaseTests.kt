package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.OneginiDeregisterUserProfileHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiDeregistrationError
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.clean.use_cases.DeregisterUserUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetUserProfileUseCase
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError
import org.junit.Assert
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
class DeregisterUserUseCaseTests {

  @get:Rule
  val reactArgumentsTestRule = ReactArgumentsTestRule()

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  lateinit var oneginiSdk: OneginiSDK

  @Mock
  lateinit var promiseMock: Promise

  @Mock
  lateinit var deregistrationError: OneginiDeregistrationError

  @Mock
  lateinit var getUserProfileUseCase: GetUserProfileUseCase

  private lateinit var deregisterUserUseCase: DeregisterUserUseCase

  @Before
  fun setup() {
    `when`(getUserProfileUseCase.invoke(any())).thenReturn(UserProfile("123456"))
    deregisterUserUseCase = DeregisterUserUseCase(oneginiSdk, getUserProfileUseCase)
  }

  @Test
  fun `when user profile cannot be created should reject with proper errors`() {
    `when`(getUserProfileUseCase.invoke(any())).thenReturn(null)

    deregisterUserUseCase("123", promiseMock)

    verify(promiseMock).reject(
      OneginiWrapperError.PROFILE_DOES_NOT_EXIST.code.toString(),
      OneginiWrapperError.PROFILE_DOES_NOT_EXIST.message
    )
  }

  @Test
  fun `should call proper SDK methods`() {
    deregisterUserUseCase("123456", promiseMock)

    argumentCaptor<UserProfile> {
      verify(oneginiSdk.oneginiClient.userClient).deregisterUser(capture(), any())

      Assert.assertEquals("123456", firstValue.profileId)
    }
  }

  @Test
  fun `when fails should reject with proper error`() {
    whenUserDeregistrationFailed()

    deregisterUserUseCase("123456", promiseMock)

    verify(oneginiSdk.oneginiClient.userClient).deregisterUser(any(), any())
    verify(promiseMock).reject("666", "MyError")
  }

  @Test
  fun `when successful should resolve with null`() {
    `when`(oneginiSdk.oneginiClient.userClient.deregisterUser(any(), any())).thenAnswer {
      it.getArgument<OneginiDeregisterUserProfileHandler>(1).onSuccess()
    }

    deregisterUserUseCase("123456", promiseMock)

    verify(oneginiSdk.oneginiClient.userClient).deregisterUser(any(), any())

    verify(promiseMock).resolve(null)
  }

  private fun whenUserDeregistrationFailed() {
    `when`(deregistrationError.errorType).thenReturn(666)
    `when`(deregistrationError.message).thenReturn("MyError")
    `when`(oneginiSdk.oneginiClient.userClient.deregisterUser(any(), any())).thenAnswer {
      it.getArgument<OneginiDeregisterUserProfileHandler>(1).onError(deregistrationError)
    }
  }
}
