package com.onegini.mobile

import android.content.Context
import android.os.Parcel
import com.facebook.react.bridge.*
import com.onegini.mobile.clean.SecurityController
import com.onegini.mobile.clean.model.SdkError
import com.onegini.mobile.clean.use_cases.GetIdentityProvidersUseCase
import com.onegini.mobile.clean.use_cases.StartClientUseCase
import com.onegini.mobile.sdk.android.client.OneginiClient
import com.onegini.mobile.sdk.android.client.UserClient
import com.onegini.mobile.sdk.android.handlers.OneginiInitializationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiInitializationError
import com.onegini.mobile.sdk.android.model.OneginiIdentityProvider
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.lenient
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*

@RunWith(MockitoJUnitRunner::class)
class GetIdentityProvidersUseCaseTests {

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
        `when`(oneginiSdk.oneginiClient).thenReturn(oneginiClient)
        `when`(oneginiSdk.oneginiClient.userClient).thenReturn(userClient)

        mockkStatic(Arguments::class)
        every { Arguments.createArray() } answers { JavaOnlyArray() }
        every { Arguments.createMap() } answers { JavaOnlyMap() }
    }

    @Test
    fun `returns proper parsed data`() {
        `when`(userClient.identityProviders).thenReturn(setOf(TestData.identityProvider1, TestData.identityProvider2))

        GetIdentityProvidersUseCase()(promiseMock)

        val provider1 = JavaOnlyMap()
        provider1.putString("id", TestData.identityProvider1.id)
        provider1.putString("name", TestData.identityProvider1.name)

        val provider2 = JavaOnlyMap()
        provider2.putString("id", TestData.identityProvider2.id)
        provider2.putString("name", TestData.identityProvider2.name)

        argumentCaptor<JavaOnlyArray> {
            verify(promiseMock).resolve(this.capture())

            Assert.assertEquals(JavaOnlyArray.of(provider1, provider2), this.firstValue)
        }
    }

    @Test
    fun `returns empty data when no providers`() {
        `when`(userClient.identityProviders).thenReturn(setOf())

        GetIdentityProvidersUseCase()(promiseMock)

        argumentCaptor<JavaOnlyArray> {
            verify(promiseMock).resolve(this.capture())

            Assert.assertEquals(JavaOnlyArray.of(), this.firstValue)
        }
    }
}