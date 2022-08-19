package com.onegini.mobile.sdk.reactnative.handlers.customregistration

import com.onegini.mobile.sdk.reactnative.model.rn.ReactNativeIdentityProvider

object SimpleCustomRegistrationFactory {

    fun getSimpleCustomRegistrationProvider(config: ReactNativeIdentityProvider): SimpleCustomRegistrationProvider {
        return SimpleCustomRegistrationProvider(getSimpleCustomRegistrationAction(config))
    }

    fun getSimpleCustomRegistrationAction(config: ReactNativeIdentityProvider): SimpleCustomRegistrationAction {
        return if (config.isTwoStep) {
            SimpleCustomTwoStepRegistrationActionImpl(config.id)
        } else {
            SimpleCustomRegistrationActionImpl(config.id)
        }
    }
}
