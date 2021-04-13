package com.onegini.mobile

import android.content.Context
import com.facebook.react.bridge.JavaOnlyMap
import com.onegini.mobile.clean.SecurityController
import com.onegini.mobile.clean.model.SdkError
import com.onegini.mobile.clean.use_cases.StartClientUseCase
import com.onegini.mobile.sdk.android.client.OneginiClient
import com.onegini.mobile.sdk.android.handlers.OneginiInitializationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiInitializationError
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.lenient
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*

@RunWith(MockitoJUnitRunner::class)
class ConfigurationTests {

    @Mock
    lateinit var context: Context

    @Mock
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var oneginiClient: OneginiClient

    @Mock
    lateinit var startCallback: (success: Boolean, error: SdkError?) -> Unit

    @Before
    fun setup() {
        OneginiComponets.init(context)
        OneginiComponets.oneginiSDK = oneginiSdk
        `when`(oneginiSdk.oneginiClient).thenReturn(oneginiClient)
    }

    @Test
    fun `startClient is successful with proper configs`() {
        // mock SDK start success
        `when`(oneginiClient.start(any())).thenAnswer {
            it.getArgument<OneginiInitializationHandler>(0).onSuccess(emptySet())
        }

        StartClientUseCase()(TestData.config, startCallback)

        verify(startCallback)(true, null)
    }

    @Test
    fun `startClient fails with wrong configs`() {
        // mock SDK start success
        lenient().`when`(oneginiClient.start(any())).thenAnswer {
            it.getArgument<OneginiInitializationHandler>(0).onSuccess(emptySet())
        }

        StartClientUseCase()(JavaOnlyMap(), startCallback)

        verify(startCallback)(false, SdkError("Exception", ""))
    }


    @Test
    fun `startClient passes errors`() {
        val error = mock<OneginiInitializationError>()
        val errorType = OneginiInitializationError.CONFIGURATION_ERROR
        `when`(error.errorType).thenReturn(errorType)

        // mock SDK start error
        `when`(oneginiClient.start(any())).thenAnswer {
            it.getArgument<OneginiInitializationHandler>(0).onError(error)
        }

        StartClientUseCase()(TestData.config, startCallback)

        verify(startCallback)(false, SdkError(errorType.toString(), "no message"))
    }

}