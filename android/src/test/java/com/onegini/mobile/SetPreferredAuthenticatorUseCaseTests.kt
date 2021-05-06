package com.onegini.mobile

import com.onegini.mobile.clean.use_cases.SetPreferredAuthenticatorUseCase
import com.onegini.mobile.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.android.model.OneginiAuthenticator
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.lenient
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class SetPreferredAuthenticatorUseCaseTests : BaseTests() {

    @Before
    fun prepareIdentityProviders() {
        lenient().`when`(userClient.userProfiles).thenReturn(setOf(UserProfile("123456")))
    }

    //

    @Test
    fun `when no profile is found rejects with error`() {
        SetPreferredAuthenticatorUseCase()("444333", "id1", promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals(OneginiWrapperErrors.USER_PROFILE_IS_NULL.code, firstValue)
            Assert.assertEquals(OneginiWrapperErrors.USER_PROFILE_IS_NULL.message, secondValue)
        }
    }

    @Test
    fun `when no registered authenticator of provided type is found reject with error`() {
        lenient().`when`(userClient.getRegisteredAuthenticators(any())).thenReturn(setOf(TestData.authenticator1, TestData.authenticator2))

        SetPreferredAuthenticatorUseCase()("123456", "id4", promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals(OneginiWrapperErrors.AUTHENTICATED_USER_PROFILE_IS_NULL.code, firstValue)
            Assert.assertEquals(OneginiWrapperErrors.AUTHENTICATED_USER_PROFILE_IS_NULL.message, secondValue)
        }
    }

    @Test
    fun `when successful should resolve with null`() {
        lenient().`when`(userClient.getRegisteredAuthenticators(any())).thenReturn(setOf(TestData.authenticator1, TestData.authenticator2, TestData.fingerprintAuthenticator))

        SetPreferredAuthenticatorUseCase()("123456", "id3", promiseMock)

        argumentCaptor<OneginiAuthenticator> {
            verify(userClient).setPreferredAuthenticator(capture())

            Assert.assertEquals("id3", firstValue.id)
        }

        verify(promiseMock).resolve(null)
    }
}
