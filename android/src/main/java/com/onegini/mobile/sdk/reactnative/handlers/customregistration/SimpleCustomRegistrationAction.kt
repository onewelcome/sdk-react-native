package com.onegini.mobile.sdk.reactnative.handlers.customregistration

import com.onegini.mobile.sdk.android.handlers.action.OneginiCustomRegistrationAction

interface SimpleCustomRegistrationAction: OneginiCustomRegistrationAction {

    fun getOneginiCustomRegistrationAction(): OneginiCustomRegistrationAction

    fun getIdProvider(): String

    fun returnSuccess(result: String?)

    fun returnError(exception: Exception?)

    fun isInProgress(): Boolean
}
