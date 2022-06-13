package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetAccessTokenUseCase
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class GetAccessTokenUseCaseTests {

    @get:Rule
    val reactArgumentsTestRule = ReactArgumentsTestRule()

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    //

    @Test
    fun `should resolve with proper access token`() {
        Mockito.`when`(oneginiSdk.oneginiClient.accessToken).thenReturn("token123")

        GetAccessTokenUseCase(oneginiSdk)(promiseMock)

        argumentCaptor<JavaOnlyArray> {
            verify(promiseMock).resolve(this.capture())

            Assert.assertEquals("token123", this.firstValue)
        }
    }

    @Test
    fun `when receive null should not crash and resolve with null`() {
        Mockito.`when`(oneginiSdk.oneginiClient.accessToken).thenReturn(null)

        GetAccessTokenUseCase(oneginiSdk)(promiseMock)

        argumentCaptor<JavaOnlyArray> {
            verify(promiseMock).resolve(this.capture())

            Assert.assertEquals(null, this.firstValue)
        }
    }
}
