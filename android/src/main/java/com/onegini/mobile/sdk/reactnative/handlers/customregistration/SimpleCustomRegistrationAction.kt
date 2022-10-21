package com.onegini.mobile.sdk.reactnative.handlers.customregistration

import com.onegini.mobile.sdk.android.handlers.action.OneginiCustomRegistrationAction

interface SimpleCustomRegistrationAction {

    fun getOneginiCustomRegistrationAction(): OneginiCustomRegistrationAction

    fun getIdProvider(): String

    fun returnSuccess(result: String?): Boolean

    fun returnError(exception: Exception?): Boolean
}
