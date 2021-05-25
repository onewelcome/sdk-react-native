package com.onegini.mobile

import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.onegini.mobile.clean.use_cases.StartClientUseCase
import com.onegini.mobile.exception.OneginReactNativeException
import com.onegini.mobile.sdk.android.client.OneginiClient
import com.onegini.mobile.sdk.android.handlers.OneginiInitializationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiInitializationError
import io.mockk.clearAllMocks
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.lenient
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*

@RunWith(MockitoJUnitRunner::class)
class StartClientUseCaseTests {

    @Mock
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var oneginiClient: OneginiClient

    @Mock
    lateinit var reactApplicationContext: ReactApplicationContext

    @Mock
    lateinit var promiseMock: Promise

    @Before
    fun setup() {
        clearAllMocks()

        `when`(oneginiSdk.oneginiClient).thenReturn(oneginiClient)
    }

    @Test
    fun `when proper configs are provided should resolve`() {
        // mock SDK start success
        `when`(oneginiClient.start(any())).thenAnswer {
            it.getArgument<OneginiInitializationHandler>(0).onSuccess(emptySet())
        }

        StartClientUseCase(oneginiSdk, reactApplicationContext)(TestData.config, promiseMock)

        verify(promiseMock).resolve(null)
    }

    @Test
    fun `when wrong configs are provided should reject`() {
        // mock SDK start success
        lenient().`when`(oneginiClient.start(any())).thenAnswer {
            it.getArgument<OneginiInitializationHandler>(0).onSuccess(emptySet())
        }

        StartClientUseCase(oneginiSdk, reactApplicationContext)(JavaOnlyMap(), promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(this.capture(), this.capture())

            Assert.assertEquals(OneginReactNativeException.WRONG_CONFIG_MODEL.toString(), this.firstValue)
            Assert.assertEquals("Provided config model parameters are wrong", this.secondValue)
        }
    }

    @Test
    fun `when oneginiClient_start fails should reject and pass proper errors`() {
        val error = mock<OneginiInitializationError>()
        val errorType = OneginiInitializationError.CONFIGURATION_ERROR
        `when`(error.errorType).thenReturn(errorType)
        `when`(error.message).thenReturn("Problem with smth")

        // mock SDK start error
        `when`(oneginiClient.start(any())).thenAnswer {
            it.getArgument<OneginiInitializationHandler>(0).onError(error)
        }

        StartClientUseCase(oneginiSdk, reactApplicationContext)(TestData.config, promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(this.capture(), this.capture())

            Assert.assertEquals(errorType.toString(), this.firstValue)
            Assert.assertEquals("Problem with smth", this.secondValue)
        }
    }

    @Test
    fun `when succeed should calls setup methods on oneginiSDK`() {
        // mock SDK start success
        `when`(oneginiClient.start(any())).thenAnswer {
            it.getArgument<OneginiInitializationHandler>(0).onSuccess(emptySet())
        }

        StartClientUseCase(oneginiSdk, reactApplicationContext)(TestData.config, promiseMock)

        verify(oneginiSdk).setPinNotificationObserver(any())
        verify(oneginiSdk).setCustomRegistrationObserver(any())
        verify(oneginiSdk).setMobileAuthOtpRequestObserver(any())
        verify(oneginiSdk).setFingerprintAuthenticationObserver(any())
    }
}
