package com.onegini.mobile.sdk.reactnative.exception

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.error.OneginiError

fun Promise.rejectWrapperError(error: OneginiWrapperError) {
    this.reject(error.code.toString(), error.message)
}

fun Promise.rejectRNException(error: OneginiReactNativeException) {
    this.reject(error.code.toString(), error.message)
}

fun Promise.rejectOneginiException(error: OneginiError) {
    this.reject(error.errorType.toString(), error.message)
}