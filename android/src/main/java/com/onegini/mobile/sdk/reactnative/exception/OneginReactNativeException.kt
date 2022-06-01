package com.onegini.mobile.sdk.reactnative.exception

import com.onegini.mobile.sdk.android.handlers.error.OneginiError
import com.onegini.mobile.sdk.android.handlers.error.OneginiErrorDetails

class OneginReactNativeException(errorType: Int, message: String): Exception(message)
