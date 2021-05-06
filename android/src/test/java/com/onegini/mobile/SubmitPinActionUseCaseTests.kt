package com.onegini.mobile

import com.onegini.mobile.clean.use_cases.SubmitPinActionUseCase
import com.onegini.mobile.view.handlers.fingerprint.FingerprintAuthenticationRequestHandler
import com.onegini.mobile.view.handlers.mobileauthotp.MobileAuthOtpRequestHandler
import com.onegini.mobile.view.handlers.pins.ChangePinHandler
import com.onegini.mobile.view.handlers.pins.CreatePinRequestHandler
import com.onegini.mobile.view.handlers.pins.PinAuthenticationRequestHandler
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.lenient
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class SubmitPinActionUseCaseTests : BaseTests() {

    @Mock
    lateinit var createPinRequestHandler: CreatePinRequestHandler

    @Mock
    lateinit var changePinHandler: ChangePinHandler

    @Mock
    lateinit var fingerprintAuthenticationRequestHandler: FingerprintAuthenticationRequestHandler

    @Mock
    lateinit var mobileAuthOtpRequestHandler: MobileAuthOtpRequestHandler

    @Mock
    lateinit var pinAuthenticationRequestHandler: PinAuthenticationRequestHandler

    @Before
    fun prepareMocks() {
        lenient().`when`(oneginiSdk.fingerprintAuthenticationRequestHandler).thenReturn(fingerprintAuthenticationRequestHandler)
        lenient().`when`(oneginiSdk.mobileAuthOtpRequestHandler).thenReturn(mobileAuthOtpRequestHandler)
        lenient().`when`(oneginiSdk.pinAuthenticationRequestHandler).thenReturn(pinAuthenticationRequestHandler)
        lenient().`when`(oneginiSdk.createPinRequestHandler).thenReturn(createPinRequestHandler)
        lenient().`when`(oneginiSdk.changePinHandler).thenReturn(changePinHandler)
    }

    @Test
    fun `when called with Authentication flow and PIN_ACTION_PROVIDE action should call accept on Pin handler with value as pin`() {
        SubmitPinActionUseCase()(Constants.PinFlow.Authentication.flowString, Constants.PIN_ACTION_PROVIDE, "123")

        verify(pinAuthenticationRequestHandler).acceptAuthenticationRequest("123".toCharArray())
    }

    @Test
    fun `when called with Authentication flow and PIN_ACTION_CANCEL action should call deny on Pin handler`() {
        SubmitPinActionUseCase()(Constants.PinFlow.Authentication.flowString, Constants.PIN_ACTION_CANCEL, "123")

        verify(pinAuthenticationRequestHandler).denyAuthenticationRequest()
    }

    @Test
    fun `when called with Create flow and PIN_ACTION_PROVIDE action should call onPinProvided on CreatePin handler`() {
        SubmitPinActionUseCase()(Constants.PinFlow.Create.flowString, Constants.PIN_ACTION_PROVIDE, "123")

        verify(createPinRequestHandler).onPinProvided("123".toCharArray(), Constants.PinFlow.Create)
    }

    @Test
    fun `when called with Create flow and PIN_ACTION_CANCEL action should call deny on CreatePin handler`() {
        SubmitPinActionUseCase()(Constants.PinFlow.Create.flowString, Constants.PIN_ACTION_CANCEL, "123")

        verify(createPinRequestHandler).pinCancelled(Constants.PinFlow.Create)
    }

    @Test
    fun `when called with Change flow and PIN_ACTION_PROVIDE action should call onPinProvided on ChangePin handler`() {
        SubmitPinActionUseCase()(Constants.PinFlow.Change.flowString, Constants.PIN_ACTION_PROVIDE, "123")

        verify(changePinHandler).onPinProvided("123".toCharArray())
    }

    @Test
    fun `when called with Change flow and PIN_ACTION_CANCEL action should call cancel on ChangePin handler`() {
        SubmitPinActionUseCase()(Constants.PinFlow.Change.flowString, Constants.PIN_ACTION_CANCEL, "123")

        verify(changePinHandler).pinCancelled()
    }
}
