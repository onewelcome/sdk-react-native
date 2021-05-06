package com.onegini.mobile

import com.onegini.mobile.clean.use_cases.IsAuthenticatorRegisteredUseCase
import com.onegini.mobile.exception.OneginiWrapperErrors
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
class IsAuthenticatorRegisteredUseCaseTests : BaseTests() {

    @Before
    fun prepareIdentityProviders() {
        lenient().`when`(userClient.userProfiles).thenReturn(setOf(UserProfile("123456")))
    }

    //

    @Test
    fun `when no profile is found rejects with error`() {
        IsAuthenticatorRegisteredUseCase()("444333", "Fingerprint", promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals(OneginiWrapperErrors.USER_PROFILE_IS_NULL.code, firstValue)
            Assert.assertEquals(OneginiWrapperErrors.USER_PROFILE_IS_NULL.message, secondValue)
        }
    }

    @Test
    fun `when no registered authenticator of provided type is found resolves with false`() {
        lenient().`when`(userClient.getRegisteredAuthenticators(any())).thenReturn(setOf(TestData.authenticator1, TestData.authenticator2))

        IsAuthenticatorRegisteredUseCase()("123456", "Fingerprint", promiseMock)

        verify(promiseMock).resolve(false)
    }

    @Test
    fun `when registered authenticator of provided type is found resolves with true`() {
        lenient().`when`(userClient.getRegisteredAuthenticators(any())).thenReturn(setOf(TestData.authenticator1, TestData.authenticator2, TestData.fingerprintAuthenticator))

        IsAuthenticatorRegisteredUseCase()("123456", "Fingerprint", promiseMock)

        verify(promiseMock).resolve(true)
    }
}
