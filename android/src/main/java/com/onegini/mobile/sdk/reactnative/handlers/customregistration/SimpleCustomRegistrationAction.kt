package com.onegini.mobile.sdk.reactnative.handlers.customregistration

import com.onegini.mobile.sdk.android.handlers.action.OneginiCustomRegistrationAction
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiCustomRegistrationCallback
import com.onegini.mobile.sdk.android.model.OneginiCustomIdentityProvider
import com.onegini.mobile.sdk.android.model.entity.CustomInfo
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors

open class SimpleCustomRegistrationAction(
    private val idProvider: String,
    private val eventEmitter: CustomRegistrationEventEmitter
) : OneginiCustomIdentityProvider,
    OneginiCustomRegistrationAction {

    protected var callback: OneginiCustomRegistrationCallback? = null

    override fun getId(): String {
        return idProvider
    }

    override fun getRegistrationAction(): OneginiCustomRegistrationAction {
        return this
    }

    override fun finishRegistration(callback: OneginiCustomRegistrationCallback, info: CustomInfo?) {
        this.callback = callback
        eventEmitter.finishRegistration(idProvider, info)
    }

    fun returnSuccess(result: String?) {
        callback?.let { customRegistrationCallback ->
            customRegistrationCallback.returnSuccess(result)
            callback = null
        } ?: throw OneginiReactNativeException(
            OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.code.toInt(),
            OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.message
        )
    }

    fun returnError(exception: Exception?) {
        callback?.let { customRegistrationCallback ->
            customRegistrationCallback.returnError(exception)
            callback = null
        } ?: throw OneginiReactNativeException(
            OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.code,
            OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.message
        )
    }

    fun isInProgress(): Boolean {
        return callback != null
    }

}
