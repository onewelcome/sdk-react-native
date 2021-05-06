package com.onegini.mobile

import com.onegini.mobile.clean.use_cases.SubmitFingerprintFallbackToPinUseCase
import com.onegini.mobile.view.handlers.fingerprint.FingerprintAuthenticationRequestHandler
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.lenient
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class SubmitFingerprintFallbackToPinUseCaseTests : BaseTests() {

    @Mock
    lateinit var fingerprintAuthenticationRequestHandler: FingerprintAuthenticationRequestHandler

    @Before
    fun prepareMocks() {
        lenient().`when`(oneginiSdk.fingerprintAuthenticationRequestHandler).thenReturn(fingerprintAuthenticationRequestHandler)
    }

    @Test
    fun `when called should call method on handler`() {
        SubmitFingerprintFallbackToPinUseCase()()

        verify(fingerprintAuthenticationRequestHandler).fallbackToPin()
    }
}
