package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.clean.use_cases.GetIdentityProvidersUseCase
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class GetIdentityProvidersUseCaseTests {

    @get:Rule
    val reactArgumentsTestRule = ReactArgumentsTestRule()

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    //

    @Test
    fun `should resolve with properly parsed data`() {
        `when`(oneginiSdk.oneginiClient.userClient.identityProviders).thenReturn(setOf(TestData.identityProvider1, TestData.identityProvider2))

        GetIdentityProvidersUseCase(oneginiSdk)(promiseMock)

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
    fun `when no providers should resolve with empty data`() {
        `when`(oneginiSdk.oneginiClient.userClient.identityProviders).thenReturn(setOf())

        GetIdentityProvidersUseCase(oneginiSdk)(promiseMock)

        argumentCaptor<JavaOnlyArray> {
            verify(promiseMock).resolve(this.capture())

            Assert.assertEquals(JavaOnlyArray.of(), this.firstValue)
        }
    }
}
