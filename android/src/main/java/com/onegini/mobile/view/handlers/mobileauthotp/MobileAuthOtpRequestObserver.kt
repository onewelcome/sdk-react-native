package com.onegini.mobile.view.handlers.mobileauthotp

import com.onegini.mobile.sdk.android.model.entity.OneginiMobileAuthenticationRequest

interface MobileAuthOtpRequestObserver{
    fun startAuthentication(request: OneginiMobileAuthenticationRequest?)

    fun finishAuthentication()
}