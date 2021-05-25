package com.onegini.mobile.view.handlers.customregistration

import com.onegini.mobile.sdk.android.handlers.action.OneginiCustomRegistrationAction

interface SimpleCustomRegistrationAction {

    fun getOneginiCustomRegistrationAction(): OneginiCustomRegistrationAction

    fun getIdProvider(): String

    fun setCustomRegistrationObserver(observer: CustomRegistrationObserver)

    fun returnSuccess(result: String?)

    fun returnError(exception: Exception?)
}
