package com.onegini.mobile.sdk.reactnative.handlers.customregistration

import com.onegini.mobile.sdk.android.handlers.action.OneginiCustomRegistrationAction
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiCustomRegistrationCallback
import com.onegini.mobile.sdk.android.model.entity.CustomInfo

class SimpleCustomRegistrationActionImpl(private val idProvider: String) : OneginiCustomRegistrationAction, SimpleCustomRegistrationAction {

    private var eventEmitter: CustomRegistrationEventEmitter = CustomRegistrationEventEmitter()
    private var callback: OneginiCustomRegistrationCallback? = null

    override fun finishRegistration(callback: OneginiCustomRegistrationCallback, info: CustomInfo?) {
        this.callback = callback
        eventEmitter.finishRegistration(idProvider, info)
    }

    override fun returnSuccess(result: String?): Boolean {
        return callback?.let { customRegistrationCallback ->
            customRegistrationCallback.returnSuccess(result)
            callback = null
            true
        } ?: false
    }

    override fun returnError(exception: Exception?): Boolean {
        callback?.let { customRegistrationCallback ->
            customRegistrationCallback.returnError(exception)
            callback = null
            return true
        }
        return false
    }

    override fun getOneginiCustomRegistrationAction(): OneginiCustomRegistrationAction {
        return this
    }

    override fun getIdProvider(): String {
        return idProvider
    }
}
