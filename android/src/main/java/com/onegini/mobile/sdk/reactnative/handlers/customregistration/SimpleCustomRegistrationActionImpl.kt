package com.onegini.mobile.sdk.reactnative.handlers.customregistration

import com.onegini.mobile.sdk.android.handlers.action.OneginiCustomRegistrationAction
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiCustomRegistrationCallback
import com.onegini.mobile.sdk.android.model.entity.CustomInfo

class SimpleCustomRegistrationActionImpl(private val idProvider: String) : OneginiCustomRegistrationAction, SimpleCustomRegistrationAction {

    var observer: CustomRegistrationObserver? = null

    var calback: OneginiCustomRegistrationCallback? = null

    override fun finishRegistration(calback: OneginiCustomRegistrationCallback, info: CustomInfo?) {
        this.calback = calback
        observer?.finishRegistration(idProvider, info)
    }

    override fun setCustomRegistrationObserver(observer: CustomRegistrationObserver) {
        this.observer = observer
    }

    override fun returnSuccess(result: String?) {
        calback?.returnSuccess(result)
    }

    override fun returnError(exception: Exception?) {
        calback?.returnError(exception)
    }

    override fun getOneginiCustomRegistrationAction(): OneginiCustomRegistrationAction {
        return this
    }

    override fun getIdProvider(): String {
        return idProvider
    }
}
