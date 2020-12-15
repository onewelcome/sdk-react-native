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
        const val AUTHENTICATE_DEVICE_ERROR = 20005
        const val CAN_NOT_DOWNLOAD_DEVICES = 20006
        const val ATTEMPT_COUNTER_ERROR = 20007
        const val PIN_ERROR_NOT_EQUAL = 20008
        const val MOBILE_AUTH_OTP_IS_DISABLED = 20009
    }

    override fun getErrorType(): Int {
        return errorType
    }
}