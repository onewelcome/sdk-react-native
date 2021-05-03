package com.onegini.mobile

import android.content.Context
import com.facebook.react.bridge.*
import com.onegini.mobile.sdk.android.client.OneginiClient
import com.onegini.mobile.sdk.android.client.UserClient
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.lenient
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
open class BaseTests {

    @Mock
    lateinit var context: Context

    @Mock
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var oneginiClient: OneginiClient

    @Mock
    lateinit var userClient: UserClient

    @Mock
    lateinit var reactApplicationContext: ReactApplicationContext

    @Mock
    lateinit var promiseMock: Promise

    @Before
    fun setup() {
        OneginiComponets.init(context)
        OneginiComponets.oneginiSDK = oneginiSdk

        lenient().`when`(oneginiSdk.oneginiClient).thenReturn(oneginiClient)
        lenient().`when`(oneginiSdk.oneginiClient.userClient).thenReturn(userClient)

        mockkStatic(Arguments::class)
        every { Arguments.createArray() } answers { JavaOnlyArray() }
        every { Arguments.createMap() } answers { JavaOnlyMap() }
    }

    @After
    fun clear() {
        clearAllMocks()
    }

    @Test
    fun test() {
        //
    }
}
