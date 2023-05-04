package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.OneginiImplicitAuthenticationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiImplicitTokenRequestError
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.clean.use_cases.AuthenticateUserImplicitlyUseCase
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError.PROFILE_DOES_NOT_EXIST
import com.onegini.mobile.sdk.reactnative.managers.AuthenticatorManager
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
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class AuthenticateUserImplicitlyUseCaseTests {

  @get:Rule
  val reactArgumentsTestRule = ReactArgumentsTestRule()

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  lateinit var oneginiSdk: OneginiSDK

  @Mock
  lateinit var promiseMock: Promise

  @Mock
  lateinit var oneginiImplicitTokenRequestError: OneginiImplicitTokenRequestError

  lateinit var authenticateUserImplicitlyUseCase: AuthenticateUserImplicitlyUseCase

  lateinit var authenticatorManager: AuthenticatorManager

  private val nonExistingProfileId = "nonExistingProfileId"
  private val existingUserProfileId = "123456"

  @Before
  fun setup() {
    authenticatorManager = AuthenticatorManager(oneginiSdk)
    authenticateUserImplicitlyUseCase = AuthenticateUserImplicitlyUseCase(oneginiSdk, authenticatorManager)
  }

  @Test
  fun `When supplying a profileId that does not exist, Then it should reject with PROFILE_DOES_NOT_EXIST`() {
    whenNoRegisteredProfiles()
    authenticateUserImplicitlyUseCase(nonExistingProfileId, null, promiseMock)
    verify(promiseMock).reject(PROFILE_DOES_NOT_EXIST.code.toString(), PROFILE_DOES_NOT_EXIST.message)
  }

  @Test
  fun `When supplying a profileId that does exist, Then it should call authenticateUserImplicitly on userClient with the correct profile`() {
    val userProfile = UserProfile(existingUserProfileId)
    whenRegisteredProfile(userProfile)
    authenticateUserImplicitlyUseCase(existingUserProfileId, null, promiseMock)
    verify(oneginiSdk.oneginiClient.userClient).authenticateUserImplicitly(eq(userProfile), any(), any())
  }

  @Test
  fun `When supplying a profileId that does exist and the handler calls onSuccess, Then the promise should resolve with null`() {
    val userProfile = UserProfile(existingUserProfileId)
    whenRegisteredProfile(userProfile)
    whenever(oneginiSdk.oneginiClient.userClient.authenticateUserImplicitly(any(), any(), any())).thenAnswer {
      it.getArgument<OneginiImplicitAuthenticationHandler>(2).onSuccess(userProfile)
    }
    authenticateUserImplicitlyUseCase(existingUserProfileId, null, promiseMock)
    verify(promiseMock).resolve(null)
  }

  @Test
  fun `When supplying a profileId that does exist and the handler calls onError, Then the promise should reject with that error`() {
    val userProfile = UserProfile(existingUserProfileId)
    whenRegisteredProfile(userProfile)
    whenever(oneginiSdk.oneginiClient.userClient.authenticateUserImplicitly(any(), any(), any())).thenAnswer {
      it.getArgument<OneginiImplicitAuthenticationHandler>(2).onError(oneginiImplicitTokenRequestError)
    }
    authenticateUserImplicitlyUseCase(existingUserProfileId, null, promiseMock)
    verify(promiseMock).reject(oneginiImplicitTokenRequestError.errorType.toString(), oneginiImplicitTokenRequestError.message)
  }

  @Test
  fun `When supplying a profileId that does exist and an array of scopes, Then it should pass all and only string values in that array to the userclient_authenticateUserImplicitly`() {
    val userProfile = UserProfile(existingUserProfileId)
    whenRegisteredProfile(userProfile)

    argumentCaptor<Array<String>> {
      whenever(oneginiSdk.oneginiClient.userClient.authenticateUserImplicitly(any(), capture(), any())).thenAnswer {
        assert(firstValue.contains("read"))
        assert(firstValue.contains("write"))
        assertEquals(firstValue.size, 2)
      }
    }
    authenticateUserImplicitlyUseCase(existingUserProfileId, JavaOnlyArray.of("read", "write", 999), promiseMock)
  }

  private fun whenNoRegisteredProfiles() {
    `when`(oneginiSdk.oneginiClient.userClient.userProfiles).thenReturn(setOf())
  }

  private fun whenRegisteredProfile(userProfile: UserProfile) {
    `when`(oneginiSdk.oneginiClient.userClient.userProfiles).thenReturn(setOf(userProfile))
  }
}
