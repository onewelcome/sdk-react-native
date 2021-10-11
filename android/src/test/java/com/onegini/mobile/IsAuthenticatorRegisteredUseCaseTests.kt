package com.onegini.mobile

import com.facebook.react.bridge.Promise
import com.onegini.mobile.clean.use_cases.GetNotRegisteredAuthenticatorForTypeUseCase
import com.onegini.mobile.clean.use_cases.IsAuthenticatorRegisteredUseCase
import com.onegini.mobile.exception.OneginiWrapperErrorException
import com.onegini.mobile.exception.OneginiWrapperErrors
import org.junit.Assert
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
class IsAuthenticatorRegisteredUseCaseTests {

    @get:Rule
    val reactArgumentsTestRule = ReactArgumentsTestRule()

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    @Mock
    lateinit var getNotRegisteredAuthenticatorForTypeUseCase: GetNotRegisteredAuthenticatorForTypeUseCase

    @Test
    fun `when no profile is found should reject with error`() {
        `when`(getNotRegisteredAuthenticatorForTypeUseCase.invoke(any(), any())).thenThrow(OneginiWrapperErrorException(OneginiWrapperErrors.USER_PROFILE_IS_NULL))
        IsAuthenticatorRegisteredUseCase(oneginiSdk, getNotRegisteredAuthenticatorForTypeUseCase)("444333", "Fingerprint", promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals(OneginiWrapperErrors.USER_PROFILE_IS_NULL.code, firstValue)
            Assert.assertEquals(OneginiWrapperErrors.USER_PROFILE_IS_NULL.message, secondValue)
        }
    }

    @Test
    fun `when no registered authenticator of provided type is found should resolve with false`() {
        `when`(getNotRegisteredAuthenticatorForTypeUseCase.invoke(any(), any())).thenThrow(OneginiWrapperErrorException(OneginiWrapperErrors.AUTHENTICATED_USER_PROFILE_IS_NULL))

        IsAuthenticatorRegisteredUseCase(oneginiSdk, getNotRegisteredAuthenticatorForTypeUseCase)("123456", "Fingerprint", promiseMock)

        verify(promiseMock).resolve(false)
    }

    @Test
    fun `when registered authenticator of provided type is found resolves with true`() {
        `when`(getNotRegisteredAuthenticatorForTypeUseCase.invoke(any(), any())).thenReturn(TestData.fingerprintAuthenticator)

        IsAuthenticatorRegisteredUseCase(oneginiSdk, getNotRegisteredAuthenticatorForTypeUseCase)("123456", "Fingerprint", promiseMock)

        verify(promiseMock).resolve(true)
    }
}
