package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.onegini.mobile.sdk.android.handlers.OneginiInitializationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiInitializationError
import com.onegini.mobile.sdk.reactnative.clean.use_cases.StartClientUseCase
import com.onegini.mobile.sdk.reactnative.exception.OneginReactNativeException
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*

@RunWith(MockitoJUnitRunner::class)
class StartClientUseCaseTests {

    @get:Rule
    val reactArgumentsTestRule = ReactArgumentsTestRule()

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    @Mock
    lateinit var reactApplicationContext: ReactApplicationContext

    //

    @Test
    fun `when proper configs are provided should resolve`() {
        // mock SDK start success
        `when`(oneginiSdk.oneginiClient.start(any())).thenAnswer {
            it.getArgument<OneginiInitializationHandler>(0).onSuccess(emptySet())
        }

        StartClientUseCase(oneginiSdk, reactApplicationContext)(TestData.config, promiseMock)

        verify(promiseMock).resolve(null)
    }

    @Test
    fun `when wrong configs are provided should reject`() {
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
        `when`(oneginiSdk.oneginiClient.start(any())).thenAnswer {
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
        `when`(oneginiSdk.oneginiClient.start(any())).thenAnswer {
            it.getArgument<OneginiInitializationHandler>(0).onSuccess(emptySet())
        }

        StartClientUseCase(oneginiSdk, reactApplicationContext)(TestData.config, promiseMock)

        verify(oneginiSdk).setPinNotificationObserver(any())
        verify(oneginiSdk).setCustomRegistrationObserver(any())
        verify(oneginiSdk).setMobileAuthOtpRequestObserver(any())
        verify(oneginiSdk).setFingerprintAuthenticationObserver(any())
    }
}
