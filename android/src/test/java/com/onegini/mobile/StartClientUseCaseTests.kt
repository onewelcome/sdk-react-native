package com.onegini.mobile

import android.content.Context
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.onegini.mobile.clean.use_cases.StartClientUseCase
import com.onegini.mobile.sdk.android.client.OneginiClient
import com.onegini.mobile.sdk.android.handlers.OneginiInitializationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiInitializationError
import io.mockk.clearAllMocks
import io.mockk.verify
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
    lateinit var context: Context

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

        OneginiComponets.init(context)
        OneginiComponets.oneginiSDK = oneginiSdk
        `when`(oneginiSdk.oneginiClient).thenReturn(oneginiClient)
    }

    @Test
    fun `is successful with proper configs`() {
        // mock SDK start success
        `when`(oneginiClient.start(any())).thenAnswer {
            it.getArgument<OneginiInitializationHandler>(0).onSuccess(emptySet())
        }

        StartClientUseCase(oneginiSdk, reactApplicationContext)(TestData.config, promiseMock)

        verify(promiseMock).resolve(null)
    }

    @Test
    fun `fails with wrong configs`() {
        // mock SDK start success
        lenient().`when`(oneginiClient.start(any())).thenAnswer {
            it.getArgument<OneginiInitializationHandler>(0).onSuccess(emptySet())
        }

        StartClientUseCase(oneginiSdk, reactApplicationContext)(JavaOnlyMap(), promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(this.capture(), this.capture())

            Assert.assertEquals("Exception", this.firstValue)
            Assert.assertEquals("", this.secondValue)
        }
    }

    @Test
    fun `when fails passed proper errors `() {
        val error = mock<OneginiInitializationError>()
        val errorType = OneginiInitializationError.CONFIGURATION_ERROR
        `when`(error.errorType).thenReturn(errorType)

        // mock SDK start error
        `when`(oneginiClient.start(any())).thenAnswer {
            it.getArgument<OneginiInitializationHandler>(0).onError(error)
        }

        StartClientUseCase(oneginiSdk, reactApplicationContext)(TestData.config, promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(this.capture(), this.capture())

            Assert.assertEquals(errorType.toString(), this.firstValue)
            Assert.assertEquals("no message", this.secondValue)
        }
    }

    @Test
    fun `if success then calls setup methods on oneginiSDK`() {
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
