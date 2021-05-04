package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.OneginiComponets
import com.onegini.mobile.sdk.android.handlers.OneginiLogoutHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiLogoutError

class LogoutUseCase {

    operator fun invoke(promise: Promise) {
        OneginiComponets.oneginiSDK.oneginiClient.userClient.logout(
            object : OneginiLogoutHandler {
                override fun onSuccess() {
                    promise.resolve(null)
                }

                override fun onError(error: OneginiLogoutError?) {
                    promise.reject(error?.errorType.toString(), error?.message)
                }
            }
        )
    }
}
