package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableArray
import com.onegini.mobile.sdk.android.handlers.OneginiDeviceAuthenticationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiDeviceAuthenticationError
import com.onegini.mobile.sdk.reactnative.clean.use_cases.AuthenticateDeviceForResourceUseCase
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class AuthenticateDeviceForResourceUseCaseTests {

    @get:Rule
    val reactArgumentsTestRule = ReactArgumentsTestRule()

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    @Mock
    lateinit var errorMock: OneginiDeviceAuthenticationError

    lateinit var authenticateDeviceForResourceUseCase: AuthenticateDeviceForResourceUseCase
    @Before
    fun setup() {
        authenticateDeviceForResourceUseCase = AuthenticateDeviceForResourceUseCase(oneginiSdk)
    }

    @Test
    fun `When supplying null as scopes, Then it should supply an empty array to deviceClient_authenticateDevice`() {
        authenticateDeviceForResourceUseCase(null, promiseMock)
        verify(oneginiSdk.oneginiClient.deviceClient).authenticateDevice(eq(arrayOf()), any())
    }

    @Test
    fun `When supplying non-null scopes, Then it should supply an array containing only the String values to deviceClient_authenticateDevice`() {
        val scopes: ReadableArray = JavaOnlyArray.of("read", "other", 1)
        authenticateDeviceForResourceUseCase(scopes, promiseMock)
        argumentCaptor<Array<String>> {
            verify(oneginiSdk.oneginiClient.deviceClient).authenticateDevice(capture(), any())
            Assert.assertEquals("read", firstValue[0])
            Assert.assertEquals("other", firstValue[1])
            Assert.assertEquals(2, firstValue.size)
        }
    }

    @Test
    fun `When onSuccess is called on the OneginiDeviceAuthenticationHandler, Then the promise should resolve with null`() {
        whenever(oneginiSdk.oneginiClient.deviceClient.authenticateDevice(any(), any())).thenAnswer {
            it.getArgument<OneginiDeviceAuthenticationHandler>(1).onSuccess()
        }
        authenticateDeviceForResourceUseCase(null, promiseMock)
        verify(promiseMock).resolve(null)
    }

    @Test
    fun `When onError is called on the OneginiDeviceAuthenticationHandler, Then the promise should reject with that error`() {
        whenever(oneginiSdk.oneginiClient.deviceClient.authenticateDevice(any(), any())).thenAnswer {
            it.getArgument<OneginiDeviceAuthenticationHandler>(1).onError(errorMock)
        }
        authenticateDeviceForResourceUseCase(null, promiseMock)
        verify(promiseMock).reject(errorMock.errorType.toString(), errorMock.message)
    }

}
