package com.onegini.mobile

import com.onegini.mobile.clean.use_cases.SubmitCustomRegistrationActionUseCase
import com.onegini.mobile.view.handlers.customregistration.SimpleCustomRegistrationAction
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class SubmitCustomRegistrationActionUseCaseTests : BaseTests() {

    @Mock
    lateinit var simpleCustomRegistrationAction: SimpleCustomRegistrationAction

    @Test
    fun `if action is found should call returnSuccess for CUSTOM_REGISTRATION_ACTION_PROVIDE`() {
        `when`(simpleCustomRegistrationAction.getIdProvider()).thenReturn("id1")
        `when`(oneginiSdk.simpleCustomRegistrationActions).thenReturn(arrayListOf(simpleCustomRegistrationAction))

        SubmitCustomRegistrationActionUseCase()(Constants.CUSTOM_REGISTRATION_ACTION_PROVIDE, "id1", "token123")

        verify(simpleCustomRegistrationAction).returnSuccess("token123")
    }

    @Test
    fun `if action is found should call returnSuccess for CUSTOM_REGISTRATION_ACTION_CANCEL`() {
        `when`(simpleCustomRegistrationAction.getIdProvider()).thenReturn("id1")
        `when`(oneginiSdk.simpleCustomRegistrationActions).thenReturn(arrayListOf(simpleCustomRegistrationAction))

        SubmitCustomRegistrationActionUseCase()(Constants.CUSTOM_REGISTRATION_ACTION_CANCEL, "id1", "token123")

        argumentCaptor<java.lang.Exception> {
            verify(simpleCustomRegistrationAction).returnError(capture())

            Assert.assertEquals("token123", firstValue.message)
        }
    }
}
