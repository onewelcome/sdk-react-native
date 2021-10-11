package com.onegini.mobile

import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.Promise
import com.onegini.mobile.clean.use_cases.GetNotRegisteredAuthenticatorForTypeUseCase
import com.onegini.mobile.clean.use_cases.RegisterAuthenticatorUseCase
import com.onegini.mobile.exception.OneginiWrapperErrorException
import com.onegini.mobile.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticatorRegistrationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiAuthenticatorRegistrationError
import com.onegini.mobile.sdk.android.model.entity.CustomInfo
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.lenient
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class RegisterAuthenticatorUseCaseTests {

    @get:Rule
    val reactArgumentsTestRule = ReactArgumentsTestRule()

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    @Mock
    lateinit var registrationError: OneginiAuthenticatorRegistrationError

    @Mock
    lateinit var getNotRegisteredAuthenticatorForTypeUseCase: GetNotRegisteredAuthenticatorForTypeUseCase

    @Test
    fun `when no profile is found should reject with error`() {
        `when`(getNotRegisteredAuthenticatorForTypeUseCase.invoke(any(), any())).thenThrow(OneginiWrapperErrorException(OneginiWrapperErrors.USER_PROFILE_IS_NULL))

        RegisterAuthenticatorUseCase(oneginiSdk, getNotRegisteredAuthenticatorForTypeUseCase)("444333", "Fingerprint", promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals(OneginiWrapperErrors.USER_PROFILE_IS_NULL.code, firstValue)
            Assert.assertEquals(OneginiWrapperErrors.USER_PROFILE_IS_NULL.message, secondValue)
        }
    }

    @Test
    fun `when no unregistered authenticator of provided type is found should reject with error`() {
        `when`(getNotRegisteredAuthenticatorForTypeUseCase.invoke(any(), any())).thenThrow(OneginiWrapperErrorException(OneginiWrapperErrors.AUTHENTICATED_USER_PROFILE_IS_NULL))

        RegisterAuthenticatorUseCase(oneginiSdk, getNotRegisteredAuthenticatorForTypeUseCase)("123456", "Fingerprint", promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals(OneginiWrapperErrors.AUTHENTICATED_USER_PROFILE_IS_NULL.code, firstValue)
            Assert.assertEquals(OneginiWrapperErrors.AUTHENTICATED_USER_PROFILE_IS_NULL.message, secondValue)
        }
    }

    @Test
    fun `when successful should resolve with custom info`() {
        `when`(getNotRegisteredAuthenticatorForTypeUseCase.invoke(any(), any())).thenReturn(TestData.fingerprintAuthenticator)

        lenient().`when`(oneginiSdk.oneginiClient.userClient.registerAuthenticator(any(), any())).thenAnswer {
            it.getArgument<OneginiAuthenticatorRegistrationHandler>(1).onSuccess(CustomInfo(200, "OK"))
        }

        RegisterAuthenticatorUseCase(oneginiSdk, getNotRegisteredAuthenticatorForTypeUseCase)("123456", "Fingerprint", promiseMock)

        argumentCaptor<JavaOnlyMap> {
            verify(promiseMock).resolve(capture())

            Assert.assertEquals(200, firstValue.getInt("status"))
            Assert.assertEquals("OK", firstValue.getString("data"))
        }
    }

    @Test
    fun `when fails should reject with proper error`() {
        whenRegisterAuthenticatorFailed()

        RegisterAuthenticatorUseCase(oneginiSdk, getNotRegisteredAuthenticatorForTypeUseCase)("123456", "Fingerprint", promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals("666", firstValue)
            Assert.assertEquals("MyError", secondValue)
        }
    }

    private fun whenRegisterAuthenticatorFailed() {
        `when`(registrationError.errorType).thenReturn(666)
        `when`(registrationError.message).thenReturn("MyError")
        `when`(getNotRegisteredAuthenticatorForTypeUseCase.invoke(any(), any())).thenReturn(TestData.fingerprintAuthenticator)
        `when`(oneginiSdk.oneginiClient.userClient.registerAuthenticator(any(), any())).thenAnswer {
            it.getArgument<OneginiAuthenticatorRegistrationHandler>(1).onError(registrationError)
        }
    }
}
