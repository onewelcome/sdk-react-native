package com.onegini.mobile.exception

import com.onegini.mobile.sdk.android.handlers.error.OneginiError
import com.onegini.mobile.sdk.android.handlers.error.OneginiErrorDetails

class OneginReactNativeException(errorType: Int, errorDetails: OneginiErrorDetails, message: String, throwable: Throwable?) :
        OneginiError(errorType, errorDetails, message, throwable) {

    companion object {
        const val PROFILE_DOES_NOT_EXIST = 20001
        const val AUTHENTICATOR_DOES_NOT_EXIST = 20002
    }

    override fun getErrorType(): Int {
        return errorType
    }
}