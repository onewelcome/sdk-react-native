package com.onegini.mobile

import com.facebook.react.bridge.Promise
import com.onegini.mobile.clean.use_cases.AuthenticateDeviceUseCase
import com.onegini.mobile.sdk.android.handlers.OneginiDeviceAuthenticationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiDeviceAuthenticationError
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
class AuthenticateDeviceUseCaseTests {

    @get:Rule
    val reactArgumentsTestRule = ReactArgumentsTestRule()

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    @Mock
    lateinit var authenticationError: OneginiDeviceAuthenticationError

    @Test
    fun `when successful should resolve with null`() {
        `when`(oneginiSdk.oneginiClient.deviceClient.authenticateDevice(any(), any())).thenAnswer {
            it.getArgument<OneginiDeviceAuthenticationHandler>(1).onSuccess()
        }

        AuthenticateDeviceUseCase(oneginiSdk)("path", promiseMock)

        argumentCaptor<Array<String>> {
            verify(oneginiSdk.oneginiClient.deviceClient).authenticateDevice(capture(), any())

            Assert.assertEquals("path", firstValue[0])
        }

        verify(promiseMock).resolve(null)
    }

    @Test
    fun `when fails should reject with proper error`() {
        `when`(authenticationError.errorType).thenReturn(666)
        `when`(authenticationError.message).thenReturn("MyError")

        `when`(oneginiSdk.oneginiClient.deviceClient.authenticateDevice(any(), any())).thenAnswer {
            it.getArgument<OneginiDeviceAuthenticationHandler>(1).onError(authenticationError)
        }

        AuthenticateDeviceUseCase(oneginiSdk)("path", promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals("666", firstValue)
            Assert.assertEquals("MyError", secondValue)
        }
    }
}
