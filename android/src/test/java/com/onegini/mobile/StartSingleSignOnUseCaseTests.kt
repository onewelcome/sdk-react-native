package com.onegini.mobile

import android.net.Uri
import com.facebook.react.bridge.JavaOnlyMap
import com.onegini.mobile.clean.use_cases.StartSingleSignOnUseCase
import com.onegini.mobile.sdk.android.handlers.OneginiAppToWebSingleSignOnHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiAppToWebSingleSignOnError
import com.onegini.mobile.sdk.android.model.OneginiAppToWebSingleSignOn
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class StartSingleSignOnUseCaseTests : BaseTests() {

    @Mock
    lateinit var app2webError: OneginiAppToWebSingleSignOnError

    @Before
    fun prepareIdentityProviders() {
        lenient().`when`(app2webError.errorType).thenReturn(666)
        lenient().`when`(app2webError.message).thenReturn("MyError")
    }

    //

    @Test
    fun `when successful should resolve with OneginiAppToWebSingleSignOn object`() {
        `when`(userClient.getAppToWebSingleSignOn(anyOrNull(), any())).thenAnswer {
            it.getArgument<OneginiAppToWebSingleSignOnHandler>(1).onSuccess(object : OneginiAppToWebSingleSignOn {
                override fun getRedirectUrl(): Uri {
                    return mock(Uri::class.java)
                }

                override fun getToken(): String {
                    return "token123"
                }
            })
        }

        StartSingleSignOnUseCase()("https://url.pl", promiseMock)

        argumentCaptor<JavaOnlyMap> {
            verify(promiseMock).resolve(capture())

            Assert.assertEquals("token123", firstValue.getString("token"))
        }
    }

    @Test
    fun `when fails rejects with proper error`() {
        `when`(userClient.getAppToWebSingleSignOn(anyOrNull(), any())).thenAnswer {
            it.getArgument<OneginiAppToWebSingleSignOnHandler>(1).onError(app2webError)
        }

        StartSingleSignOnUseCase()("https://url.pl", promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals("666", firstValue)
            Assert.assertEquals("MyError", secondValue)
        }
    }
}
