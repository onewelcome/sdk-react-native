package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.MOBILE_AUTH_OTP_NOT_IN_PROGRESS
import com.onegini.mobile.sdk.reactnative.handlers.mobileauthotp.MobileAuthOtpRequestHandler
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DenyMobileAuthConfirmationUseCase @Inject constructor(private val mobileAuthOtpRequestHandler: MobileAuthOtpRequestHandler) {
    operator fun invoke(promise: Promise) {
            tryDenyAuthenticationRequest(promise)
    }

    private fun tryDenyAuthenticationRequest(promise: Promise) {
        if (mobileAuthOtpRequestHandler.denyAuthenticationRequest()) {
            promise.resolve(null)
        } else {
            promise.reject(MOBILE_AUTH_OTP_NOT_IN_PROGRESS.code.toString(), MOBILE_AUTH_OTP_NOT_IN_PROGRESS.message)
        }
    }
}