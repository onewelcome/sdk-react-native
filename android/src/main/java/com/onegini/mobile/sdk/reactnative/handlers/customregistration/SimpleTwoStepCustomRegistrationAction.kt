package com.onegini.mobile.sdk.reactnative.handlers.customregistration

import com.onegini.mobile.sdk.android.handlers.action.OneginiCustomTwoStepRegistrationAction
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiCustomRegistrationCallback
import com.onegini.mobile.sdk.android.model.entity.CustomInfo

class SimpleTwoStepCustomRegistrationAction(
    private val idProvider: String,
    private val eventEmitter: CustomRegistrationEventEmitter
) : OneginiCustomTwoStepRegistrationAction, SimpleCustomRegistrationAction(idProvider, eventEmitter) {

    override fun initRegistration(callback: OneginiCustomRegistrationCallback, info: CustomInfo?) {
        this.callback = callback
        eventEmitter.initRegistration(idProvider, info)
    }
}
