package com.onegini.mobile.sdk.reactnative.handlers.customregistration

import com.onegini.mobile.sdk.android.handlers.action.OneginiCustomRegistrationAction
import com.onegini.mobile.sdk.android.model.OneginiCustomIdentityProvider

class SimpleCustomRegistrationProvider(val action: SimpleCustomRegistrationAction) : OneginiCustomIdentityProvider {

    override fun getId(): String {
        return action.getIdProvider()
    }

    override fun getRegistrationAction(): OneginiCustomRegistrationAction {
        return action.getOneginiCustomRegistrationAction()
    }
}
