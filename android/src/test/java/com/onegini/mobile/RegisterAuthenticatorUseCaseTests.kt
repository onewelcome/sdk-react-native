package com.onegini.mobile

import com.facebook.react.bridge.JavaOnlyMap
import com.onegini.mobile.clean.use_cases.RegisterAuthenticatorUseCase
import com.onegini.mobile.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticatorRegistrationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiAuthenticatorRegistrationError
import com.onegini.mobile.sdk.android.model.entity.CustomInfo
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.lenient
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class RegisterAuthenticatorUseCaseTests : BaseTests() {

    @Mock
    lateinit var registrationError: OneginiAuthenticatorRegistrationError

    @Before
    fun prepareIdentityProviders() {
        lenient().`when`(registrationError.errorType).thenReturn(666)
        lenient().`when`(registrationError.message).thenReturn("MyError")

        lenient().`when`(userClient.userProfiles).thenReturn(setOf(UserProfile("123456")))
    }

    //

    @Test
    fun `when no profile is found rejects with error`() {
        RegisterAuthenticatorUseCase()("444333", "Fingerprint", promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals(OneginiWrapperErrors.USER_PROFILE_IS_NULL.code, firstValue)
            Assert.assertEquals(OneginiWrapperErrors.USER_PROFILE_IS_NULL.message, secondValue)
        }
    }

    @Test
    fun `when no unregistered authenticator of provided type is found reject with error`() {
        lenient().`when`(userClient.getNotRegisteredAuthenticators(any())).thenReturn(setOf(TestData.authenticator1, TestData.authenticator2))

        RegisterAuthenticatorUseCase()("123456", "Fingerprint", promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals(OneginiWrapperErrors.AUTHENTICATED_USER_PROFILE_IS_NULL.code, firstValue)
            Assert.assertEquals(OneginiWrapperErrors.AUTHENTICATED_USER_PROFILE_IS_NULL.message, secondValue)
        }
    }

    @Test
    fun `when successful should resolve with custom info`() {
        lenient().`when`(userClient.getNotRegisteredAuthenticators(any())).thenReturn(setOf(TestData.authenticator1, TestData.authenticator2, TestData.fingerprintAuthenticator))
        lenient().`when`(userClient.registerAuthenticator(any(), any())).thenAnswer {
            it.getArgument<OneginiAuthenticatorRegistrationHandler>(1).onSuccess(CustomInfo(200, "OK"))
        }

        RegisterAuthenticatorUseCase()("123456", "Fingerprint", promiseMock)

        argumentCaptor<JavaOnlyMap> {
            verify(promiseMock).resolve(capture())

            Assert.assertEquals(200, firstValue.getInt("status"))
            Assert.assertEquals("OK", firstValue.getString("data"))
        }
    }

    @Test
    fun `when fails rejects with proper error`() {
        lenient().`when`(userClient.getNotRegisteredAuthenticators(any())).thenReturn(setOf(TestData.authenticator1, TestData.authenticator2, TestData.fingerprintAuthenticator))
        lenient().`when`(userClient.registerAuthenticator(any(), any())).thenAnswer {
            it.getArgument<OneginiAuthenticatorRegistrationHandler>(1).onError(registrationError)
        }

        RegisterAuthenticatorUseCase()("123456", "Fingerprint", promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals("666", firstValue)
            Assert.assertEquals("MyError", secondValue)
        }
    }
}
