package com.onegini.mobile.sdk.reactnative.handlers.customregistration

import com.onegini.mobile.sdk.android.handlers.action.OneginiCustomRegistrationAction
import com.onegini.mobile.sdk.android.handlers.action.OneginiCustomTwoStepRegistrationAction
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiCustomRegistrationCallback
import com.onegini.mobile.sdk.android.model.entity.CustomInfo
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors

class SimpleCustomTwoStepRegistrationActionImpl(private val idProvider: String) : OneginiCustomTwoStepRegistrationAction, SimpleCustomRegistrationAction {

    private var eventEmitter: CustomRegistrationEventEmitter = CustomRegistrationEventEmitter()
    private var callback: OneginiCustomRegistrationCallback? = null

    override fun initRegistration(callback: OneginiCustomRegistrationCallback, info: CustomInfo?) {
        this.callback = callback
        eventEmitter.initRegistration(idProvider, info)
    }

    override fun finishRegistration(callback: OneginiCustomRegistrationCallback, info: CustomInfo?) {
        this.callback = callback
        eventEmitter.finishRegistration(idProvider, info)
    }

    override fun getOneginiCustomRegistrationAction(): OneginiCustomRegistrationAction {
        return this
    }

    override fun getIdProvider(): String {
        return idProvider
    }

    override fun returnSuccess(result: String?): Boolean {
        return callback?.let { customRegistrationCallback ->
            customRegistrationCallback.returnSuccess(result)
            callback = null
            true
        } ?: false
    }

    @Throws(OneginiReactNativeException::class)
    override fun returnError(exception: Exception?) {
        callback?.let { customRegistrationCallback ->
            customRegistrationCallback.returnError(exception)
            callback = null
        } ?: throw OneginiReactNativeException(
            OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.code,
            OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.message
        )
    }
}
