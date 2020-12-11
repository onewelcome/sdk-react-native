package com.onegini.mobile.exception

import com.onegini.mobile.sdk.android.handlers.error.OneginiError
import com.onegini.mobile.sdk.android.handlers.error.OneginiErrorDetails
import org.bouncycastle.util.Fingerprint

class OneginReactNativeException(errorType: Int, errorDetails: OneginiErrorDetails, message: String, throwable: Throwable?) :
        OneginiError(errorType, errorDetails, message, throwable) {

    companion object {
        const val PROFILE_DOES_NOT_EXIST = 20001
        const val AUTHENTICATOR_DOES_NOT_EXIST = 20002
        const val FINGERPRINT_IS_NOT_ENABLED = 20003
        const val IMPLICIT_USER_DETAILS_ERROR = 20004
    }

    override fun getErrorType(): Int {
        return errorType
    }
}