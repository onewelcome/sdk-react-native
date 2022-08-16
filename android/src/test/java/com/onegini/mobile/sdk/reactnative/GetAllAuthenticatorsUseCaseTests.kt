package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetAllAuthenticatorsUseCase
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetUserProfileUseCase
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.android.model.entity.UserProfile
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
class GetAllAuthenticatorsUseCaseTests {

    @get:Rule
    val reactArgumentsTestRule = ReactArgumentsTestRule()

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    @Mock
    lateinit var getUserProfileUseCase: GetUserProfileUseCase

    private lateinit var getAllAuthenticatorsUseCase: GetAllAuthenticatorsUseCase

    @Before
    fun setup() {
        getAllAuthenticatorsUseCase = GetAllAuthenticatorsUseCase(oneginiSdk, getUserProfileUseCase)
    }

    @Test
    fun `when no profile is found should reject with error`() {
        getAllAuthenticatorsUseCase("profileId1", promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            assertEquals(OneginiWrapperErrors.USER_PROFILE_IS_NULL.code, firstValue)
            assertEquals(OneginiWrapperErrors.USER_PROFILE_IS_NULL.message, secondValue)
        }
    }

    @Test
    fun `should resolve with list of authenticators for specific user profile`() {
        `when`(oneginiSdk.oneginiClient.userClient.getAllAuthenticators(any())).thenReturn(setOf(TestData.authenticator1, TestData.authenticator2))

        `when`(getUserProfileUseCase.invoke(any())).thenReturn(UserProfile("123456"))

        getAllAuthenticatorsUseCase("123456", promiseMock)

        argumentCaptor<JavaOnlyArray> {
            verify(promiseMock).resolve(capture())

            assertEquals(2, firstValue.size())

            assertEquals(TestData.authenticator1.id, firstValue.getMap(0)?.getString("id"))
            assertEquals(TestData.authenticator1.name, firstValue.getMap(0)?.getString("name"))
            assertEquals(TestData.authenticator1.type, firstValue.getMap(0)?.getInt("type"))
            assertEquals(TestData.authenticator1.isPreferred, firstValue.getMap(0)?.getBoolean("isPreferred"))
            assertEquals(TestData.authenticator1.isRegistered, firstValue.getMap(0)?.getBoolean("isRegistered"))
            assertEquals(TestData.authenticator1.userProfile.profileId, firstValue.getMap(0)?.getMap("userProfile")?.getString("profileId"))

            assertEquals(TestData.authenticator2.id, firstValue.getMap(1)?.getString("id"))
            assertEquals(TestData.authenticator2.name, firstValue.getMap(1)?.getString("name"))
            assertEquals(TestData.authenticator2.type, firstValue.getMap(1)?.getInt("type"))
            assertEquals(TestData.authenticator2.isPreferred, firstValue.getMap(1)?.getBoolean("isPreferred"))
            assertEquals(TestData.authenticator2.isRegistered, firstValue.getMap(1)?.getBoolean("isRegistered"))
            assertEquals(TestData.authenticator2.userProfile.profileId, firstValue.getMap(1)?.getMap("userProfile")?.getString("profileId"))
        }
    }
}
