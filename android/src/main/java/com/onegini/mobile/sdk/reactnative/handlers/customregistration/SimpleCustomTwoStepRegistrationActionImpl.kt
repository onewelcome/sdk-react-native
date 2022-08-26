package com.onegini.mobile.sdk.reactnative.handlers.customregistration

import com.onegini.mobile.sdk.android.handlers.action.OneginiCustomRegistrationAction
import com.onegini.mobile.sdk.android.handlers.action.OneginiCustomTwoStepRegistrationAction
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiCustomRegistrationCallback
import com.onegini.mobile.sdk.android.model.entity.CustomInfo

class SimpleCustomTwoStepRegistrationActionImpl(private val idProvider: String) : OneginiCustomTwoStepRegistrationAction, SimpleCustomRegistrationAction {

    var eventEmitter: CustomRegistrationEventEmitter = CustomRegistrationEventEmitter()

    var calback: OneginiCustomRegistrationCallback? = null

    override fun initRegistration(calback: OneginiCustomRegistrationCallback, info: CustomInfo?) {
        this.calback = calback
        eventEmitter.initRegistration(idProvider, info)
    }

    override fun finishRegistration(calback: OneginiCustomRegistrationCallback, info: CustomInfo?) {
        this.calback = calback
        eventEmitter.finishRegistration(idProvider, info)
    }

    override fun getOneginiCustomRegistrationAction(): OneginiCustomRegistrationAction {
        return this
    }

    override fun getIdProvider(): String {
        return idProvider
    }

    override fun returnSuccess(result: String?) {
        calback?.returnSuccess(result)
    }

    override fun returnError(exception: Exception?) {
        calback?.returnError(exception)
    }
}
