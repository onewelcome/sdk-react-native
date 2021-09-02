package com.onegini.mobile.sdk.reactnative.handlers.customregistration

import com.onegini.mobile.sdk.android.handlers.action.OneginiCustomRegistrationAction

interface SimpleCustomRegistrationAction {

    fun getOneginiCustomRegistrationAction(): OneginiCustomRegistrationAction

    fun getIdProvider(): String

    fun setCustomRegistrationObserver(observer: CustomRegistrationObserver)

    fun returnSuccess(result: String?)

    fun returnError(exception: Exception?)
}
