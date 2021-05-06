package com.onegini.mobile

import com.onegini.mobile.clean.use_cases.DenyAuthenticationRequestUseCase
import com.onegini.mobile.view.handlers.fingerprint.FingerprintAuthenticationRequestHandler
import com.onegini.mobile.view.handlers.mobileauthotp.MobileAuthOtpRequestHandler
import com.onegini.mobile.view.handlers.pins.PinAuthenticationRequestHandler
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class DenyAuthenticationRequestUseCaseTests : BaseTests() {

    @Mock
    lateinit var fingerprintAuthenticationRequestHandler: FingerprintAuthenticationRequestHandler

    @Mock
    lateinit var mobileAuthOtpRequestHandler: MobileAuthOtpRequestHandler

    @Mock
    lateinit var pinAuthenticationRequestHandler: PinAuthenticationRequestHandler

    @Before
    fun prepareMocks() {
        Mockito.lenient().`when`(oneginiSdk.fingerprintAuthenticationRequestHandler).thenReturn(fingerprintAuthenticationRequestHandler)
        Mockito.lenient().`when`(oneginiSdk.mobileAuthOtpRequestHandler).thenReturn(mobileAuthOtpRequestHandler)
        Mockito.lenient().`when`(oneginiSdk.pinAuthenticationRequestHandler).thenReturn(pinAuthenticationRequestHandler)
    }

    @Test
    fun `when called with Pin type should call Pin handler`() {
        DenyAuthenticationRequestUseCase()("Pin")

        verify(pinAuthenticationRequestHandler).denyAuthenticationRequest()
    }

    @Test
    fun `when called with Fingerprint type should call Fingerprint handler`() {
        DenyAuthenticationRequestUseCase()("Fingerprint")

        verify(fingerprintAuthenticationRequestHandler).denyAuthenticationRequest()
    }

    @Test
    fun `when called with MobileAuthOtp type should call MobileAuthOtp handler`() {
        DenyAuthenticationRequestUseCase()("MobileAuthOtp")

        verify(mobileAuthOtpRequestHandler).denyAuthenticationRequest()
    }
}
