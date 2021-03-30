package com.onegini.mobile.exception

import com.onegini.mobile.sdk.android.handlers.error.OneginiError
import com.onegini.mobile.sdk.android.handlers.error.OneginiErrorDetails
import org.bouncycastle.util.Fingerprint

class OneginReactNativeException(errorType: Int, errorDetails: OneginiErrorDetails, message: String, throwable: Throwable?) :
        OneginiError(errorType, errorDetails, message, throwable) {

    companion object {
        const val PROFILE_DOES_NOT_EXIST = 8001
        const val AUTHENTICATOR_DOES_NOT_EXIST = 8002
        const val FINGERPRINT_IS_NOT_ENABLED = 8003
        const val IMPLICIT_USER_DETAILS_ERROR = 8004
        const val AUTHENTICATE_DEVICE_ERROR = 8005
        const val ATTEMPT_COUNTER_ERROR = 8007
        const val PIN_ERROR_NOT_EQUAL = 8008
        const val MOBILE_AUTH_OTP_IS_DISABLED = 8009
    }

    override fun getErrorType(): Int {
        return errorType
    }
}