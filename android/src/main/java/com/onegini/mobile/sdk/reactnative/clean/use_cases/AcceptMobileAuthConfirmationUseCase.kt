package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.reactnative.handlers.mobileauthotp.MobileAuthOtpRequestHandler
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AcceptMobileAuthConfirmationUseCase @Inject constructor(
    private val oneginiSDK: OneginiSDK,
    private val mobileAuthOtpRequestHandler: MobileAuthOtpRequestHandler
) {
    operator fun invoke(promise: Promise) {
        if (oneginiSDK.config.enableMobileAuthenticationOtp) {
            tryAcceptAuthenticationRequest(promise)
        } else {
            promise.reject(OneginiWrapperErrors.MOBILE_AUTH_OTP_IS_DISABLED.code.toString(), OneginiWrapperErrors.MOBILE_AUTH_OTP_IS_DISABLED.message)
        }
    }

    private fun tryAcceptAuthenticationRequest(promise: Promise) {
        try {
            mobileAuthOtpRequestHandler.acceptAuthenticationRequest()
            promise.resolve(null)
        } catch (exception: OneginiReactNativeException) {
            promise.reject(exception.errorType.toString(), exception.message)
        }
    }
}
