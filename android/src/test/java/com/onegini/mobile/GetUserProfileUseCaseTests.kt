package com.onegini.mobile

import com.onegini.mobile.clean.use_cases.GetUserProfileUseCase
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.lenient
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class GetUserProfileUseCaseTests : BaseTests() {

    @Test
    fun `if id is null returns null`() {
        lenient().`when`(userClient.userProfiles).thenReturn(setOf(UserProfile("123456"), UserProfile("234567")))

        val profile = GetUserProfileUseCase()(null)

        Assert.assertEquals(null, profile)
    }

    @Test
    fun `returns profile for given id`() {
        lenient().`when`(userClient.userProfiles).thenReturn(setOf(UserProfile("123456"), UserProfile("234567")))

        val profile = GetUserProfileUseCase()("123456")

        Assert.assertEquals("123456", profile!!.profileId)
    }

    @Test
    fun `if no profile is found returns null`() {
        lenient().`when`(userClient.userProfiles).thenReturn(setOf(UserProfile("123456"), UserProfile("234567")))

        val profile = GetUserProfileUseCase()("123")

        Assert.assertEquals(null, profile)
    }
}
