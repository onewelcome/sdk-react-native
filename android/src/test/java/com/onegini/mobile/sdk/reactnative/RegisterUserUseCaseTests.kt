package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableArray
import com.onegini.mobile.sdk.android.handlers.OneginiRegistrationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiRegistrationError
import com.onegini.mobile.sdk.android.model.OneginiIdentityProvider
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.clean.use_cases.RegisterUserUseCase
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.lenient
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class RegisterUserUseCaseTests {

    @get:Rule
    val reactArgumentsTestRule = ReactArgumentsTestRule()

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    @Mock
    lateinit var identityProvider1: OneginiIdentityProvider

    @Mock
    lateinit var identityProvider2: OneginiIdentityProvider

    @Mock
    lateinit var registrationError: OneginiRegistrationError

    lateinit var scopes: ReadableArray

    @Before
    fun setup() {
        lenient().`when`(identityProvider1.id).thenReturn("id1")
        lenient().`when`(identityProvider1.name).thenReturn("name1")

        lenient().`when`(identityProvider2.id).thenReturn("id2")
        lenient().`when`(identityProvider2.name).thenReturn("name2")

        lenient().`when`(registrationError.errorType).thenReturn(666)
        lenient().`when`(registrationError.message).thenReturn("MyError")

        `when`(oneginiSdk.oneginiClient.userClient.identityProviders).thenReturn(setOf(identityProvider1, identityProvider2))

        scopes = JavaOnlyArray.of("read")
    }

    //

    @Test
    fun `when onSuccess should resolve with user profile with proper id`() {
        `when`(oneginiSdk.oneginiClient.userClient.registerUser(anyOrNull(), any(), any())).thenAnswer {
            it.getArgument<OneginiRegistrationHandler>(2).onSuccess(UserProfile("123456"), null)
        }

        RegisterUserUseCase(oneginiSdk)("id1", scopes, promiseMock)

        argumentCaptor<JavaOnlyMap> {
            verify(promiseMock).resolve(capture())

            Assert.assertEquals("123456", firstValue.getString("profileId"))
        }
    }

    @Test
    fun `when no provider is provided should call registerUser with null identity provider`() {
        RegisterUserUseCase(oneginiSdk)(null, scopes, promiseMock)

        argumentCaptor<OneginiIdentityProvider> {
            verify(oneginiSdk.oneginiClient.userClient).registerUser(capture(), any(), any())

            Assert.assertEquals(null, firstValue)
        }
    }

    @Test
    fun `when provider with provided id is not found should reject with proper errors`() {
        RegisterUserUseCase(oneginiSdk)("someId", scopes, promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals(OneginiWrapperErrors.IDENTITY_PROVIDER_NOT_FOUND.code, firstValue)
            Assert.assertEquals(OneginiWrapperErrors.IDENTITY_PROVIDER_NOT_FOUND.message, secondValue)
        }
    }

    @Test
    fun `when failed should reject with proper errors`() {
        `when`(oneginiSdk.oneginiClient.userClient.registerUser(anyOrNull(), any(), any())).thenAnswer {
            it.getArgument<OneginiRegistrationHandler>(2).onError(registrationError)
        }

        RegisterUserUseCase(oneginiSdk)("id1", scopes, promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals("666", firstValue)
            Assert.assertEquals("MyError", secondValue)
        }
    }

    @Test
    fun `should call registerUser with identity provider with provided id and with scopes`() {
        scopes = JavaOnlyArray.of("read", "other")

        RegisterUserUseCase(oneginiSdk)("id1", scopes, promiseMock)

        argumentCaptor<OneginiIdentityProvider> {
            verify(oneginiSdk.oneginiClient.userClient).registerUser(capture(), any(), any())

            Assert.assertEquals("id1", firstValue.id)
            Assert.assertEquals("name1", firstValue.name)
        }

        argumentCaptor<Array<String>> {
            verify(oneginiSdk.oneginiClient.userClient).registerUser(any(), capture(), any())

            Assert.assertEquals("read", firstValue[0])
            Assert.assertEquals("other", firstValue[1])
        }
    }
}
