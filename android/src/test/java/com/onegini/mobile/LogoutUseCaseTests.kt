package com.onegini.mobile

import com.facebook.react.bridge.Promise
import com.onegini.mobile.clean.use_cases.LogoutUseCase
import com.onegini.mobile.sdk.android.handlers.OneginiLogoutHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiLogoutError
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
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

    private lateinit var logoutUseCase: LogoutUseCase

    @Before
    fun setup() {
        logoutUseCase = LogoutUseCase(oneginiSdk)
    }

    @Test
    fun `when success should resolve with null`() {
        `when`(oneginiSdk.oneginiClient.userClient.logout(any())).thenAnswer {
            it.getArgument<OneginiLogoutHandler>(0).onSuccess()
        }

        logoutUseCase(promiseMock)

        verify(oneginiSdk.oneginiClient.userClient).logout(any())

        verify(promiseMock).resolve(null)
    }

    @Test
    fun `should call proper methods`() {
        logoutUseCase(promiseMock)

        verify(oneginiSdk.oneginiClient.userClient).logout(any())
    }

    @Test
    fun `when fails should reject with proper error`() {
        whenLogoutFailed()

        logoutUseCase(promiseMock)
        verify(promiseMock).reject("666", "MyError")
    }

    private fun whenLogoutFailed() {
        `when`(logoutError.errorType).thenReturn(666)
        `when`(logoutError.message).thenReturn("MyError")
        `when`(oneginiSdk.oneginiClient.userClient.logout(any())).thenAnswer {
            it.getArgument<OneginiLogoutHandler>(0).onError(logoutError)
        }
    }
}
