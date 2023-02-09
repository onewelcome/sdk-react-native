package com.onegini.mobile.sdk.reactnative.handlers.customregistration

import com.onegini.mobile.sdk.reactnative.model.rn.ReactNativeIdentityProvider
import javax.inject.Inject

class CustomRegistrationActionFactory @Inject constructor(private val customRegistrationEventEmitter: CustomRegistrationEventEmitter) {

    fun createCustomRegistrationAction(config: ReactNativeIdentityProvider): CustomRegistrationAction {
        return if (config.isTwoStep) {
            TwoStepCustomRegistrationAction(config.id, customRegistrationEventEmitter)
        } else {
            CustomRegistrationAction(config.id, customRegistrationEventEmitter)
        }
    }
}