package com.onegini.mobile

import com.onegini.mobile.clean.use_cases.AuthenticateDeviceForResourceUseCase
import com.onegini.mobile.sdk.android.handlers.OneginiDeviceAuthenticationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiDeviceAuthenticationError
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class AuthenticateDeviceForResourceUseCaseTests : BaseTests() {

    @Mock
    lateinit var authenticationError: OneginiDeviceAuthenticationError

    @Before
    fun prepareIdentityProviders() {
        Mockito.lenient().`when`(authenticationError.errorType).thenReturn(666)
        Mockito.lenient().`when`(authenticationError.message).thenReturn("MyError")

        Mockito.lenient().`when`(userClient.userProfiles).thenReturn(setOf(UserProfile("123456")))
    }

    //

    @Test
    fun `when successful should resolve with null`() {
        Mockito.`when`(deviceClient.authenticateDevice(any(), any())).thenAnswer {
            it.getArgument<OneginiDeviceAuthenticationHandler>(1).onSuccess()
        }

        AuthenticateDeviceForResourceUseCase()("path", promiseMock)

        argumentCaptor<Array<String>> {
            verify(deviceClient).authenticateDevice(capture(), any())

            Assert.assertEquals("path", firstValue[0])
        }

        verify(promiseMock).resolve(null)
    }

    @Test
    fun `when fails rejects with proper error`() {
        Mockito.`when`(deviceClient.authenticateDevice(any(), any())).thenAnswer {
            it.getArgument<OneginiDeviceAuthenticationHandler>(1).onError(authenticationError)
        }

        AuthenticateDeviceForResourceUseCase()("path", promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals("666", firstValue)
            Assert.assertEquals("MyError", secondValue)
        }
    }
}
