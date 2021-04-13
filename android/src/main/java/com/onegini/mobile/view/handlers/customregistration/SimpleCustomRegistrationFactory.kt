package com.onegini.mobile.view.handlers.customregistration

import com.onegini.mobile.model.rn.ReactNativeIdentityProvider

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