package com.onegini.mobile

import com.onegini.mobile.clean.use_cases.LogoutUseCase
import com.onegini.mobile.sdk.android.handlers.OneginiLogoutHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiLogoutError
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.lenient
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class LogoutUseCaseTests : BaseTests() {

    @Mock
    lateinit var logoutError: OneginiLogoutError

    @Before
    fun prepareIdentityProviders() {
        lenient().`when`(logoutError.errorType).thenReturn(666)
        lenient().`when`(logoutError.message).thenReturn("MyError")
    }

    //

    @Test
    fun `when success should resolve with null`() {
        `when`(userClient.logout(any())).thenAnswer {
            it.getArgument<OneginiLogoutHandler>(0).onSuccess()
        }

        LogoutUseCase()(promiseMock)

        verify(userClient).logout(any())

        verify(promiseMock).resolve(null)
    }

    @Test
    fun `when fails rejects with proper error`() {
        `when`(userClient.logout(any())).thenAnswer {
            it.getArgument<OneginiLogoutHandler>(0).onError(logoutError)
        }

        LogoutUseCase()(promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals("666", firstValue)
            Assert.assertEquals("MyError", secondValue)
        }
    }
}
