package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.OneginiComponets
import com.onegini.mobile.sdk.android.handlers.OneginiMobileAuthWithOtpHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiMobileAuthWithOtpError

class HandleMobileAuthWithOtpUseCase {

    operator fun invoke(otpCode: String, promise: Promise) {
        OneginiComponets.oneginiSDK.oneginiClient.userClient.handleMobileAuthWithOtp(
            otpCode,
            object : OneginiMobileAuthWithOtpHandler {
                override fun onSuccess() {
                    promise.resolve(null)
                }

                override fun onError(error: OneginiMobileAuthWithOtpError?) {
                    promise.reject(error?.errorType.toString(), error?.message)
                }
            }
        )
    }
}
