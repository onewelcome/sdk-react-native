package com.onegini.mobile.sdk.reactnative.exception

class OneginiReactNativeException constructor(var code: Int, message: String): Exception(message) {
    constructor(error: OneginiWrapperError) : this(error.code, error.message)
}