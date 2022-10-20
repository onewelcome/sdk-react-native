package com.onegini.mobile.sdk.reactnative

import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetUserProfileUseCase
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito.lenient
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GetUserProfileUseCaseTests {

    @get:Rule
    val reactArgumentsTestRule = ReactArgumentsTestRule()

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Test
    fun `should return profile for given id`() {
        lenient().`when`(oneginiSdk.oneginiClient.userClient.userProfiles).thenReturn(setOf(UserProfile("123456"), UserProfile("234567")))

        val profile = GetUserProfileUseCase(oneginiSdk)("123456")

        Assert.assertEquals("123456", profile!!.profileId)
    }

    @Test
    fun `when no profile is found should return null`() {
        lenient().`when`(oneginiSdk.oneginiClient.userClient.userProfiles).thenReturn(setOf(UserProfile("123456"), UserProfile("234567")))

        val profile = GetUserProfileUseCase(oneginiSdk)("123")

        Assert.assertEquals(null, profile)
    }
}
