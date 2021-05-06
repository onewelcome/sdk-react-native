package com.onegini.mobile

import com.onegini.mobile.clean.use_cases.AcceptAuthenticationRequestUseCase
import com.onegini.mobile.view.handlers.fingerprint.FingerprintAuthenticationRequestHandler
import com.onegini.mobile.view.handlers.mobileauthotp.MobileAuthOtpRequestHandler
import com.onegini.mobile.view.handlers.pins.PinAuthenticationRequestHandler
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.lenient
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class AcceptAuthenticationRequestUseCaseTests : BaseTests() {

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
    }

    @Test
    fun `when called with Pin type should call Pin handler with value as pin`() {
        AcceptAuthenticationRequestUseCase()("Pin", "1234")

        verify(pinAuthenticationRequestHandler).acceptAuthenticationRequest("1234".toCharArray())
    }

    @Test
    fun `when called with Fingerprint type should call Fingerprint handler with value as pin`() {
        AcceptAuthenticationRequestUseCase()("Fingerprint", null)

        verify(fingerprintAuthenticationRequestHandler).acceptAuthenticationRequest()
    }

    @Test
    fun `when called with MobileAuthOtp type should call MobileAuthOtp handler with value as pin`() {
        AcceptAuthenticationRequestUseCase()("MobileAuthOtp", null)

        verify(mobileAuthOtpRequestHandler).acceptAuthenticationRequest()
    }
}
