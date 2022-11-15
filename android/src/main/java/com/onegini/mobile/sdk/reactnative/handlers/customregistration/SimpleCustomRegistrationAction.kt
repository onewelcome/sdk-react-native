package com.onegini.mobile.sdk.reactnative.handlers.customregistration

import com.onegini.mobile.sdk.android.handlers.action.OneginiCustomRegistrationAction
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException

interface SimpleCustomRegistrationAction {

    fun getOneginiCustomRegistrationAction(): OneginiCustomRegistrationAction

    fun getIdProvider(): String

    @Throws(OneginiReactNativeException::class)
    fun returnSuccess(result: String?)

    @Throws(OneginiReactNativeException::class)
    fun returnError(exception: Exception?)
}
