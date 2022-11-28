package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.MOBILE_AUTH_OTP_IS_DISABLED
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.MOBILE_AUTH_OTP_NOT_IN_PROGRESS
import com.onegini.mobile.sdk.reactnative.handlers.mobileauthotp.MobileAuthOtpRequestHandler
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DenyMobileAuthConfirmationUseCase @Inject constructor(
    private val oneginiSDK: OneginiSDK,
    private val mobileAuthOtpRequestHandler: MobileAuthOtpRequestHandler
) {
    operator fun invoke(promise: Promise) {
        when (oneginiSDK.config.enableMobileAuthenticationOtp) {
            false -> promise.reject(MOBILE_AUTH_OTP_IS_DISABLED.code, MOBILE_AUTH_OTP_IS_DISABLED.message)
            true -> {
                when (mobileAuthOtpRequestHandler.denyAuthenticationRequest()) {
                    true -> promise.resolve(null)
                    false -> promise.reject(MOBILE_AUTH_OTP_NOT_IN_PROGRESS.code, MOBILE_AUTH_OTP_NOT_IN_PROGRESS.message)
                }
            }
        }
    }
}