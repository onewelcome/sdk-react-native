package com.onegini.mobile.sdk.reactnative.handlers.customregistration

import com.onegini.mobile.sdk.reactnative.model.rn.ReactNativeIdentityProvider
import javax.inject.Inject

class SimpleCustomRegistrationFactory @Inject constructor(private val customRegistrationEventEmitter: CustomRegistrationEventEmitter) {

    fun getSimpleCustomRegistrationProvider(config: ReactNativeIdentityProvider): SimpleCustomRegistrationProvider {
        return SimpleCustomRegistrationProvider(getSimpleCustomRegistrationAction(config))
    }

    fun getSimpleCustomRegistrationAction(config: ReactNativeIdentityProvider): SimpleCustomRegistrationAction {
        return if (config.isTwoStep) {
            SimpleCustomTwoStepRegistrationActionImpl(config.id, customRegistrationEventEmitter)
        } else {
            SimpleCustomRegistrationActionImpl(config.id, customRegistrationEventEmitter)
        }
    }
}
