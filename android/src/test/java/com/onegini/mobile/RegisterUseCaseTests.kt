package com.onegini.mobile

import com.facebook.react.bridge.*
import com.onegini.mobile.clean.use_cases.RegisterUserUseCase
import com.onegini.mobile.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.android.handlers.OneginiRegistrationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiRegistrationError
import com.onegini.mobile.sdk.android.model.OneginiIdentityProvider
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.lenient
import org.mockito.kotlin.*

class RegisterUseCaseTests : BaseTests() {

    @Mock
    lateinit var identityProvider1: OneginiIdentityProvider

    @Mock
    lateinit var identityProvider2: OneginiIdentityProvider

    @Mock
    lateinit var registrationError: OneginiRegistrationError

    @Before
    fun prepareIdentityProviders() {
        lenient().`when`(identityProvider1.id).thenReturn("id1")
        lenient().`when`(identityProvider1.name).thenReturn("name1")

        lenient().`when`(identityProvider2.id).thenReturn("id2")
        lenient().`when`(identityProvider2.name).thenReturn("name2")

        lenient().`when`(registrationError.errorType).thenReturn(666)
        lenient().`when`(registrationError.message).thenReturn("MyError")

        `when`(userClient.identityProviders).thenReturn(setOf(identityProvider1, identityProvider2))
    }

    //
    //
    //

    @Test
    fun `when successful returns user profile with proper id`() {
        `when`(userClient.registerUser(anyOrNull(), any(), any())).thenAnswer {
            it.getArgument<OneginiRegistrationHandler>(2).onSuccess(UserProfile("123456"), null)
        }

        RegisterUserUseCase()("id1", promiseMock)

        argumentCaptor<JavaOnlyMap> {
            verify(promiseMock).resolve(capture())

            Assert.assertEquals("123456", firstValue.getString("profileId"))
        }
    }

    @Test
    fun `calls with null identity provider if no provider provided`() {
        RegisterUserUseCase()(null, promiseMock)

        argumentCaptor<OneginiIdentityProvider> {
            verify(userClient).registerUser(capture(), any(), any())

            Assert.assertEquals(null, firstValue)
        }
    }

    @Test
    fun `if provider with provided id is not found rejects with proper errors`() {
        RegisterUserUseCase()("someId", promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals(OneginiWrapperErrors.IDENTITY_PROVIDER_NOT_FOUND.code, firstValue)
            Assert.assertEquals(OneginiWrapperErrors.IDENTITY_PROVIDER_NOT_FOUND.message, secondValue)
        }
    }

    @Test
    fun `if failed rejects with proper errors`() {
        `when`(userClient.registerUser(anyOrNull(), any(), any())).thenAnswer {
            it.getArgument<OneginiRegistrationHandler>(2).onError(registrationError)
        }

        RegisterUserUseCase()("id1", promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals("666", firstValue)
            Assert.assertEquals("MyError", secondValue)
        }
    }

    @Test
    fun `calls with identity provider with provided id`() {
        RegisterUserUseCase()("id1", promiseMock)

        argumentCaptor<OneginiIdentityProvider> {
            verify(userClient).registerUser(capture(), any(), any())

            Assert.assertEquals("id1", firstValue.id)
            Assert.assertEquals("name1", firstValue.name)
        }
    }
}
