package com.onegini.mobile

import com.facebook.react.bridge.Promise
import com.onegini.mobile.clean.use_cases.LogoutUseCase
import com.onegini.mobile.sdk.android.handlers.OneginiLogoutHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiLogoutError
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class LogoutUseCaseTests {

    @get:Rule
    val reactArgumentsTestRule = ReactArgumentsTestRule()

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    @Mock
    lateinit var logoutError: OneginiLogoutError

    @Test
    fun `when success should resolve with null`() {
        `when`(oneginiSdk.oneginiClient.userClient.logout(any())).thenAnswer {
            it.getArgument<OneginiLogoutHandler>(0).onSuccess()
        }

        LogoutUseCase(oneginiSdk)(promiseMock)

        verify(oneginiSdk.oneginiClient.userClient).logout(any())

        verify(promiseMock).resolve(null)
    }

    @Test
    fun `should call proper methods`() {
        LogoutUseCase(oneginiSdk)(promiseMock)

        verify(oneginiSdk.oneginiClient.userClient).logout(any())
    }

    @Test
    fun `when fails should reject with proper error`() {
        `when`(logoutError.errorType).thenReturn(666)
        `when`(logoutError.message).thenReturn("MyError")

        `when`(oneginiSdk.oneginiClient.userClient.logout(any())).thenAnswer {
            it.getArgument<OneginiLogoutHandler>(0).onError(logoutError)
        }

        LogoutUseCase(oneginiSdk)(promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals("666", firstValue)
            Assert.assertEquals("MyError", secondValue)
        }
    }
}
