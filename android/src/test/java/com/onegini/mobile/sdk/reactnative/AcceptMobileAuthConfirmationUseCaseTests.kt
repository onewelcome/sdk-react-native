package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiAcceptDenyCallback
import com.onegini.mobile.sdk.android.model.entity.OneginiMobileAuthenticationRequest
import com.onegini.mobile.sdk.reactnative.clean.use_cases.AcceptMobileAuthConfirmationUseCase
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.MOBILE_AUTH_OTP_IS_DISABLED
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.MOBILE_AUTH_OTP_NOT_IN_PROGRESS
import com.onegini.mobile.sdk.reactnative.handlers.mobileauthotp.MobileAuthOtpRequestEventEmitter
import com.onegini.mobile.sdk.reactnative.handlers.mobileauthotp.MobileAuthOtpRequestHandler
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class AcceptMobileAuthConfirmationUseCaseTests {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    @Mock
    lateinit var mobileAuthOtpRequestEventEmitter: MobileAuthOtpRequestEventEmitter

    @Mock
    lateinit var oneginiMobileAuthenticationRequest: OneginiMobileAuthenticationRequest

    @Mock
    lateinit var oneginiAcceptDenyCallback: OneginiAcceptDenyCallback

    lateinit var mobileAuthOtpRequestHandler: MobileAuthOtpRequestHandler

    @Before
    fun setup() {
        mobileAuthOtpRequestHandler = MobileAuthOtpRequestHandler(mobileAuthOtpRequestEventEmitter)
    }

    @Test
    fun `When mobile authentication with OTP is not enabled, Then promise should reject with that error`() {
        disabledMobileAuthentication()
        AcceptMobileAuthConfirmationUseCase(oneginiSdk, mobileAuthOtpRequestHandler)(promiseMock)
        verify(promiseMock).reject(MOBILE_AUTH_OTP_IS_DISABLED.code, MOBILE_AUTH_OTP_IS_DISABLED.message)
    }

    @Test
    fun `When mobile authentication with OTP is enabled and is in progress, Then should reject with that error`() {
        enabledMobileAuthentication()
        AcceptMobileAuthConfirmationUseCase(oneginiSdk, mobileAuthOtpRequestHandler)(promiseMock)
        verify(promiseMock).reject(MOBILE_AUTH_OTP_NOT_IN_PROGRESS.code, MOBILE_AUTH_OTP_NOT_IN_PROGRESS.message)
    }

    @Test
    fun `When mobile authentication with OTP is enabled and is in progress, Then should resolve with null`() {
        enabledMobileAuthentication()
        startMobileAuthentication()
        AcceptMobileAuthConfirmationUseCase(oneginiSdk, mobileAuthOtpRequestHandler)(promiseMock)
        verify(promiseMock).resolve(null)
    }

    private fun startMobileAuthentication() {
        mobileAuthOtpRequestHandler.startAuthentication(oneginiMobileAuthenticationRequest, oneginiAcceptDenyCallback)
    }

    private fun disabledMobileAuthentication() {
        `when`(oneginiSdk.config.enableMobileAuthenticationOtp).thenReturn(false)
    }

    private fun enabledMobileAuthentication() {
        `when`(oneginiSdk.config.enableMobileAuthenticationOtp).thenReturn(true)
    }

}
