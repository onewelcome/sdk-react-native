package com.onegini.mobile

import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.Promise
import com.onegini.mobile.clean.use_cases.GetRegisteredAuthenticatorsUseCase
import com.onegini.mobile.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito
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

    @Test
    fun `when no profile is found should reject with error`() {
        Mockito.lenient().`when`(oneginiSdk.oneginiClient.userClient.getRegisteredAuthenticators(any())).thenReturn(setOf(TestData.authenticator1, TestData.authenticator2))

        GetRegisteredAuthenticatorsUseCase(oneginiSdk)("profileId1", promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals(OneginiWrapperErrors.USER_PROFILE_IS_NULL.code, firstValue)
            Assert.assertEquals(OneginiWrapperErrors.USER_PROFILE_IS_NULL.message, secondValue)
        }
    }

    @Test
    fun `should resolve with list of authenticators for specific user profile`() {
        Mockito.lenient().`when`(oneginiSdk.oneginiClient.userClient.getRegisteredAuthenticators(any())).thenReturn(setOf(TestData.authenticator1, TestData.authenticator2))

        Mockito.`when`(oneginiSdk.oneginiClient.userClient.userProfiles).thenReturn(setOf(UserProfile("123456"), UserProfile("234567")))

        GetRegisteredAuthenticatorsUseCase(oneginiSdk)("123456", promiseMock)

        argumentCaptor<JavaOnlyArray> {
            verify(promiseMock).resolve(capture())

            Assert.assertEquals(TestData.authenticator1.id, firstValue.getMap(0)?.getString("id"))
            Assert.assertEquals(TestData.authenticator1.name, firstValue.getMap(0)?.getString("name"))
            Assert.assertEquals(TestData.authenticator1.type, firstValue.getMap(0)?.getInt("type"))
            Assert.assertEquals(TestData.authenticator1.isPreferred, firstValue.getMap(0)?.getBoolean("isPreferred"))
            Assert.assertEquals(TestData.authenticator1.isRegistered, firstValue.getMap(0)?.getBoolean("isRegistered"))
            Assert.assertEquals(TestData.authenticator1.userProfile.profileId, firstValue.getMap(0)?.getMap("userProfile")?.getString("profileId"))

            Assert.assertEquals(TestData.authenticator2.id, firstValue.getMap(1)?.getString("id"))
            Assert.assertEquals(TestData.authenticator2.name, firstValue.getMap(1)?.getString("name"))
            Assert.assertEquals(TestData.authenticator2.type, firstValue.getMap(1)?.getInt("type"))
            Assert.assertEquals(TestData.authenticator2.isPreferred, firstValue.getMap(1)?.getBoolean("isPreferred"))
            Assert.assertEquals(TestData.authenticator2.isRegistered, firstValue.getMap(1)?.getBoolean("isRegistered"))
            Assert.assertEquals(TestData.authenticator2.userProfile.profileId, firstValue.getMap(1)?.getMap("userProfile")?.getString("profileId"))
        }
    }
}
