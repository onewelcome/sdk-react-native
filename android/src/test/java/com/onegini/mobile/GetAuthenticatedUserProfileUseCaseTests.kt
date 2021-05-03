package com.onegini.mobile

import com.facebook.react.bridge.JavaOnlyMap
import com.onegini.mobile.clean.use_cases.GetAuthenticatedUserProfileUseCase
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class GetAuthenticatedUserProfileUseCaseTests : BaseTests() {

    @Test
    fun `returns properly parsed profile data`() {
        Mockito.`when`(userClient.authenticatedUserProfile).thenReturn(UserProfile("testId"))

        GetAuthenticatedUserProfileUseCase()(promiseMock)

        argumentCaptor<JavaOnlyMap> {
            verify(promiseMock).resolve(this.capture())

            Assert.assertEquals("testId", this.firstValue.getString("profileId"))
        }
    }

    @Test
    fun `if authenticatedUserProfile is null returns profileId as null`() {
        Mockito.`when`(userClient.authenticatedUserProfile).thenReturn(null)

        GetAuthenticatedUserProfileUseCase()(promiseMock)

        argumentCaptor<JavaOnlyMap> {
            verify(promiseMock).resolve(this.capture())

            Assert.assertEquals(null, this.firstValue.getString("profileId"))
        }
    }
}
