package com.onegini.mobile.sdk.reactnative.exception

class OneginiReactNativeException constructor(var errorType: Int, message: String): Exception(message) {
    constructor(error: OneginiWrapperErrors) : this(error.code, error.message)
}
