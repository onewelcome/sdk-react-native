package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetAuthenticatedUserProfileUseCase
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class GetAuthenticatedUserProfileUseCaseTests {

    @get:Rule
    val reactArgumentsTestRule = ReactArgumentsTestRule()

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    @Test
    fun `when success should resolve with properly parsed profile data`() {
        Mockito.`when`(oneginiSdk.oneginiClient.userClient.authenticatedUserProfile).thenReturn(UserProfile("testId"))

        GetAuthenticatedUserProfileUseCase(oneginiSdk)(promiseMock)

        argumentCaptor<JavaOnlyMap> {
            verify(promiseMock).resolve(this.capture())

            Assert.assertEquals("testId", this.firstValue.getString("profileId"))
        }
    }

    @Test
    fun `when authenticatedUserProfile is null should resolve with profileId as null`() {
        Mockito.`when`(oneginiSdk.oneginiClient.userClient.authenticatedUserProfile).thenReturn(null)

        GetAuthenticatedUserProfileUseCase(oneginiSdk)(promiseMock)

        argumentCaptor<JavaOnlyMap> {
            verify(promiseMock).resolve(this.capture())

            Assert.assertEquals(null, this.firstValue.getString("profileId"))
        }
    }
}
