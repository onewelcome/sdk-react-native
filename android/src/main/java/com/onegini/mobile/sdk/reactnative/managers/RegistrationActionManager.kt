package com.onegini.mobile.sdk.reactnative.managers

import com.onegini.mobile.sdk.reactnative.handlers.customregistration.CustomRegistrationAction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegistrationActionManager @Inject constructor() {

    private val customRegistrationActions = ArrayList<CustomRegistrationAction>()

    fun getCustomRegistrationAction(id: String?): CustomRegistrationAction? {
        for (action in customRegistrationActions) {
            if (action.id == id) {
                return action
            }
        }
        return null
    }

    fun getCustomRegistrationActions(): ArrayList<CustomRegistrationAction> {
        return customRegistrationActions
    }

    fun addCustomRegistrationAction(action: CustomRegistrationAction) {
        customRegistrationActions.add((action))
    }

    fun getActiveCustomRegistrationAction(): CustomRegistrationAction? {
        customRegistrationActions.forEach { action ->
            if (action.isInProgress()){
                return action
            }
        }
        return null
    }
}
