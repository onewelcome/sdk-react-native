package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.sdk.android.handlers.OneginiMobileAuthEnrollmentHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiMobileAuthEnrollmentError

class EnrollMobileAuthenticationUseCase(private val oneginiSDK: OneginiSDK) {

    operator fun invoke(promise: Promise) {
        oneginiSDK.oneginiClient.userClient.enrollUserForMobileAuth(object : OneginiMobileAuthEnrollmentHandler {
            override fun onSuccess() {
                promise.resolve(null)
            }

            override fun onError(error: OneginiMobileAuthEnrollmentError?) {
                promise.reject(error?.errorType.toString(), error?.message)
            }
        })
    }
}
