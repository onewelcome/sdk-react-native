package com.onegini.mobile

import com.onegini.mobile.clean.use_cases.StartChangePinFlowUseCase
import com.onegini.mobile.sdk.android.handlers.error.OneginiChangePinError
import com.onegini.mobile.view.handlers.pins.ChangePinHandler
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
class StartChangePinFlowUseCaseTests : BaseTests() {

    @Mock
    lateinit var changePinError: OneginiChangePinError

    @Mock
    lateinit var changePinHandler: ChangePinHandler

    @Before
    fun prepareIdentityProviders() {
        lenient().`when`(changePinError.errorType).thenReturn(666)
        lenient().`when`(changePinError.message).thenReturn("MyError")

        lenient().`when`(oneginiSdk.changePinHandler).thenReturn(changePinHandler)
    }

    //

    @Test
    fun `when fails rejects with proper error`() {
        `when`(changePinHandler.onStartChangePin(any())).thenAnswer {
            it.getArgument<ChangePinHandler.ChangePinHandlerResponse>(0).onError(changePinError)
        }

        StartChangePinFlowUseCase()(promiseMock)

        argumentCaptor<String> {
            verify(promiseMock).reject(capture(), capture())

            Assert.assertEquals("666", firstValue)
            Assert.assertEquals("MyError", secondValue)
        }
    }

    @Test
    fun `when successful resolves with null`() {
        `when`(changePinHandler.onStartChangePin(any())).thenAnswer {
            it.getArgument<ChangePinHandler.ChangePinHandlerResponse>(0).onSuccess()
        }

        StartChangePinFlowUseCase()(promiseMock)

        verify(promiseMock).resolve(null)
    }
}
