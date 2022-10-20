package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.onegini.mobile.sdk.android.handlers.OneginiInitializationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiInitializationError
import com.onegini.mobile.sdk.reactnative.clean.use_cases.StartClientUseCase
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors
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

        StartClientUseCase(oneginiSdk)(TestData.config, promiseMock)

        verify(promiseMock).resolve(null)
    }

    @Test
    fun `when wrong configs are provided should reject`() {
        StartClientUseCase(oneginiSdk)(JavaOnlyMap(), promiseMock)
        verify(promiseMock).reject(OneginiWrapperErrors.WRONG_CONFIG_MODEL.code, OneginiWrapperErrors.WRONG_CONFIG_MODEL.message)
    }
    
    @Test
    fun `when oneginiClient_start fails should reject and pass proper errors`() {
        val error = mock<OneginiInitializationError>()
        val errorType = OneginiInitializationError.GENERAL_ERROR
        `when`(error.errorType).thenReturn(errorType)
        `when`(error.message).thenReturn("Problem with smth")
        // mock SDK start error
        `when`(oneginiSdk.oneginiClient.start(any())).thenAnswer {
            it.getArgument<OneginiInitializationHandler>(0).onError(error)
        }

        StartClientUseCase(oneginiSdk)(TestData.config, promiseMock)
        verify(promiseMock).reject(errorType.toString(), "Problem with smth")
    }
}
