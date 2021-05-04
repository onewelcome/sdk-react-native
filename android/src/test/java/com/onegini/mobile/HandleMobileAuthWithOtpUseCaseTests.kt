package com.onegini.mobile

import com.onegini.mobile.clean.use_cases.HandleMobileAuthWithOtpUseCase
import com.onegini.mobile.sdk.android.handlers.OneginiMobileAuthWithOtpHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiMobileAuthWithOtpError
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class HandleMobileAuthWithOtpUseCaseTests : BaseTests() {

    @Mock
    lateinit var mobileAuthOtpError: OneginiMobileAuthWithOtpError

    @Before
    fun prepareIdentityProviders() {
        Mockito.lenient().`when`(mobileAuthOtpError.errorType).thenReturn(666)
        Mockito.lenient().`when`(mobileAuthOtpError.message).thenReturn("MyError")

        Mockito.lenient().`when`(userClient.userProfiles).thenReturn(setOf(UserProfile("123456")))
    }

    //

    @Test
    fun `when successful should resolve with null`() {
        `when`(userClient.handleMobileAuthWithOtp(any(), any())).thenAnswer {
            it.getArgument<OneginiMobileAuthWithOtpHandler>(1).onSuccess()
        }

        HandleMobileAuthWithOtpUseCase()("code123", promiseMock)

        verify(promiseMock).resolve(null)
    }

    @Test
    fun `when fails rejects with proper error`() {
        `when`(userClient.handleMobileAuthWithOtp(any(), any())).thenAnswer {
            it.getArgument<OneginiMobileAuthWithOtpHandler>(1).onError(mobileAuthOtpError)
        }

        HandleMobileAuthWithOtpUseCase()("code123", promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals("666", firstValue)
            Assert.assertEquals("MyError", secondValue)
        }
    }
}
