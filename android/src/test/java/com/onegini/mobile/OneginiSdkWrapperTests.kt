package com.onegini.mobile

import android.content.Context
import com.facebook.react.bridge.*
import com.onegini.mobile.clean.use_cases.GetIdentityProvidersUseCase
import com.onegini.mobile.clean.use_cases.StartClientUseCase
import com.onegini.mobile.clean.wrapper.OneginiSdkWrapper
import com.onegini.mobile.sdk.android.client.OneginiClient
import com.onegini.mobile.sdk.android.client.UserClient
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class OneginiSdkWrapperTests {

    @Mock
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var reactApplicationContext: ReactApplicationContext

    @Mock
    lateinit var promiseMock: Promise

    @Mock
    lateinit var startClientUseCase: StartClientUseCase

    @Mock
    lateinit var getIdentityProvidersUseCase: GetIdentityProvidersUseCase

    @Before
    fun setup() {
        clearAllMocks()

        mockkStatic(Arguments::class)
        every { Arguments.createArray() } answers { JavaOnlyArray() }
        every { Arguments.createMap() } answers { JavaOnlyMap() }
    }

    @Test
    fun `when startClient method is called calls startClientUseCase with proper params`() {
        val wrapper = OneginiSdkWrapper(oneginiSdk, reactApplicationContext, startClientUseCase, getIdentityProvidersUseCase)

        wrapper.startClient(JavaOnlyMap(), promiseMock)

        verify(startClientUseCase).invoke(JavaOnlyMap(), promiseMock)
    }

    @Test
    fun `when getIdentityProviders method is called calls getIdentityProvidersUseCase with proper params`() {
        val wrapper = OneginiSdkWrapper(oneginiSdk, reactApplicationContext, startClientUseCase, getIdentityProvidersUseCase)

        wrapper.getIdentityProviders(promiseMock)

        verify(getIdentityProvidersUseCase).invoke(promiseMock)
    }

}