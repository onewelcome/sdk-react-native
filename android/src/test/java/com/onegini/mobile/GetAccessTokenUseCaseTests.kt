package com.onegini.mobile

import android.content.Context
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.Promise
import com.onegini.mobile.clean.use_cases.GetAccessTokenUseCase
import com.onegini.mobile.clean.use_cases.GetIdentityProvidersUseCase
import com.onegini.mobile.sdk.android.client.OneginiClient
import com.onegini.mobile.sdk.android.client.UserClient
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class GetAccessTokenUseCaseTests {

    @Mock
    lateinit var context: Context

    @Mock
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var oneginiClient: OneginiClient

    @Mock
    lateinit var userClient: UserClient

    @Mock
    lateinit var promiseMock: Promise

    @Before
    fun setup() {
        clearAllMocks()

        OneginiComponets.init(context)
        OneginiComponets.oneginiSDK = oneginiSdk
        Mockito.`when`(oneginiSdk.oneginiClient).thenReturn(oneginiClient)

        mockkStatic(Arguments::class)
        every { Arguments.createArray() } answers { JavaOnlyArray() }
        every { Arguments.createMap() } answers { JavaOnlyMap() }
    }

    @Test
    fun `returns proper access token`() {
        Mockito.`when`(oneginiClient.accessToken).thenReturn("token123")

        GetAccessTokenUseCase()(promiseMock)

        argumentCaptor<JavaOnlyArray> {
            verify(promiseMock).resolve(this.capture())

            Assert.assertEquals("token123", this.firstValue)
        }
    }

    @Test
    fun `does not crash when receive null`() {
        Mockito.`when`(oneginiClient.accessToken).thenReturn(null)

        GetAccessTokenUseCase()(promiseMock)

        argumentCaptor<JavaOnlyArray> {
            verify(promiseMock).resolve(this.capture())

            Assert.assertEquals(null, this.firstValue)
        }
    }
}