package com.onegini.mobile.sdk.reactnative.handlers.mobileauthotp

import com.onegini.mobile.sdk.android.handlers.request.OneginiMobileAuthWithOtpRequestHandler
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiAcceptDenyCallback
import com.onegini.mobile.sdk.android.model.entity.OneginiMobileAuthenticationRequest

class MobileAuthOtpRequestHandler : OneginiMobileAuthWithOtpRequestHandler {

    var eventEmitter: MobileAuthOtpRequestEventEmitter = MobileAuthOtpRequestEventEmitter()

    private var callback: OneginiAcceptDenyCallback? = null

    override fun startAuthentication(request: OneginiMobileAuthenticationRequest, callback: OneginiAcceptDenyCallback) {
        this.callback = callback
        eventEmitter.startAuthentication(request)
    }

    override fun finishAuthentication() {
        eventEmitter.finishAuthentication()
    }

    fun acceptAuthenticationRequest(): Boolean {
        callback?.let { authCallback ->
            authCallback.acceptAuthenticationRequest()
            return true
        }
        return false
    }

    fun denyAuthenticationRequest(): Boolean {
        callback?.let { authCallback ->
            authCallback.denyAuthenticationRequest()
            return true
        }
        return false
    }
}
