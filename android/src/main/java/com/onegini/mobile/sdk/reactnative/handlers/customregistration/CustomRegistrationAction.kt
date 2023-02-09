package com.onegini.mobile.sdk.reactnative.handlers.customregistration

import com.onegini.mobile.sdk.android.handlers.action.OneginiCustomRegistrationAction
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiCustomRegistrationCallback
import com.onegini.mobile.sdk.android.model.OneginiCustomIdentityProvider
import com.onegini.mobile.sdk.android.model.entity.CustomInfo
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.*

open class CustomRegistrationAction(
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
        } ?: throw OneginiReactNativeException(REGISTRATION_NOT_IN_PROGRESS)
    }

    fun returnError(exception: Exception?) {
        callback?.let { customRegistrationCallback ->
            customRegistrationCallback.returnError(exception)
            callback = null
        } ?: throw OneginiReactNativeException(REGISTRATION_NOT_IN_PROGRESS)
    }

    fun isInProgress(): Boolean {
        return callback != null
    }

}
