package com.onegini.mobile

import android.net.Uri
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.Promise
import com.onegini.mobile.clean.use_cases.StartSingleSignOnUseCase
import com.onegini.mobile.sdk.android.handlers.OneginiAppToWebSingleSignOnHandler
import com.onegini.mobile.sdk.android.handlers.OneginiMobileAuthWithOtpHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiAppToWebSingleSignOnError
import com.onegini.mobile.sdk.android.model.OneginiAppToWebSingleSignOn
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class StartSingleSignOnUseCaseTests {

    @get:Rule
    val reactArgumentsTestRule = ReactArgumentsTestRule()

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    @Mock
    lateinit var app2webError: OneginiAppToWebSingleSignOnError

    @Test
    fun `when successful should resolve with OneginiAppToWebSingleSignOn object`() {
        `when`(oneginiSdk.oneginiClient.userClient.getAppToWebSingleSignOn(anyOrNull(), any())).thenAnswer {
            it.getArgument<OneginiAppToWebSingleSignOnHandler>(1).onSuccess(object : OneginiAppToWebSingleSignOn {
                override fun getRedirectUrl(): Uri {
                    return mock(Uri::class.java)
                }

                override fun getToken(): String {
                    return "token123"
                }
            })
        }

        StartSingleSignOnUseCase(oneginiSdk)("https://url.pl", promiseMock)

        argumentCaptor<JavaOnlyMap> {
            verify(promiseMock).resolve(capture())

            Assert.assertEquals("token123", firstValue.getString("token"))
        }
    }

    @Test
    fun `when fails rejects with proper error`() {
        whenStartSingleSignOnFailed()

        StartSingleSignOnUseCase(oneginiSdk)("https://url.pl", promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals("666", firstValue)
            Assert.assertEquals("MyError", secondValue)
        }
    }

    private fun whenStartSingleSignOnFailed() {
        `when`(app2webError.errorType).thenReturn(666)
        `when`(app2webError.message).thenReturn("MyError")
        `when`(oneginiSdk.oneginiClient.userClient.getAppToWebSingleSignOn(anyOrNull(), any())).thenAnswer {
            it.getArgument<OneginiAppToWebSingleSignOnHandler>(1).onError(app2webError)
        }
    }
}
