package com.onegini.mobile.sdk.reactnative.handlers.mobileauthotp

import com.onegini.mobile.sdk.android.handlers.request.OneginiMobileAuthWithOtpRequestHandler
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiAcceptDenyCallback
import com.onegini.mobile.sdk.android.model.entity.OneginiMobileAuthenticationRequest
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.MOBILE_AUTH_OTP_NOT_IN_PROGRESS
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MobileAuthOtpRequestHandler @Inject constructor(private val eventEmitter: MobileAuthOtpRequestEventEmitter):
    OneginiMobileAuthWithOtpRequestHandler {

    private var callback: OneginiAcceptDenyCallback? = null

    override fun startAuthentication(request: OneginiMobileAuthenticationRequest, callback: OneginiAcceptDenyCallback) {
        this.callback = callback
        eventEmitter.startAuthentication(request)
    }

    override fun finishAuthentication() {
        eventEmitter.finishAuthentication()
    }

    @Throws(OneginiReactNativeException::class)
    fun acceptAuthenticationRequest() {
        callback?.let { authCallback ->
            authCallback.acceptAuthenticationRequest()
            callback = null
        } ?: throw OneginiReactNativeException(MOBILE_AUTH_OTP_NOT_IN_PROGRESS)
    }

    fun denyAuthenticationRequest(): Boolean {
        callback?.let { authCallback ->
            authCallback.denyAuthenticationRequest()
            return true
        }
        return false
    }
}
