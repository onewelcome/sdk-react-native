package com.onegini.mobile.sdk.reactnative.exception

import com.onegini.mobile.sdk.android.handlers.error.OneginiError
import com.onegini.mobile.sdk.android.handlers.error.OneginiErrorDetails

@Deprecated("Please use OneginiWrapperErrors if possible")
class OneginReactNativeException(errorType: Int, message: String): Exception(message) {

    companion object {
        const val PROFILE_DOES_NOT_EXIST = 8001
        const val AUTHENTICATOR_DOES_NOT_EXIST = 8002
    }
}
