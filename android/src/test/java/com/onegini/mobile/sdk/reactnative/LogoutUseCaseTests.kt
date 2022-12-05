package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.OneginiLogoutHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiLogoutError
import com.onegini.mobile.sdk.reactnative.clean.use_cases.LogoutUseCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)

class LogoutUseCaseTests {
    @get:Rule
    val reactArgumentsTestRule = ReactArgumentsTestRule()

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private lateinit var oneginiSdk: OneginiSDK

    @Mock
    private lateinit var oneginiLogoutError: OneginiLogoutError

    @Mock
    private lateinit var promiseMock: Promise

    private lateinit var logoutUseCase: LogoutUseCase

    @Before
    fun setup() {
        logoutUseCase = LogoutUseCase(oneginiSdk)
    }

    @Test
    fun `When oginini getAppToWebSingleSignOn calls onSuccess on the handler, Then promise should resolve with a map containing the content from the result`() {
        whenever(oneginiSdk.oneginiClient.userClient.logout(any())).thenAnswer {
            it.getArgument<OneginiLogoutHandler>(0).onSuccess()
        }
        logoutUseCase(promiseMock)
        verify(promiseMock).resolve(null)
    }

    @Test
    fun `When oginini getAppToWebSingleSignOn calls onSuccess on the handler, Then promise should reject with the error message and code`() {
        whenever(oneginiSdk.oneginiClient.userClient.logout(any())).thenAnswer {
            it.getArgument<OneginiLogoutHandler>(0).onError(oneginiLogoutError)
        }
        logoutUseCase(promiseMock)
        verify(promiseMock).reject(oneginiLogoutError.errorType.toString(), oneginiLogoutError.message)
    }
}
