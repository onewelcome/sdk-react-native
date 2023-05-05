package com.onegini.mobile.sdk.reactnative.managers

import com.onegini.mobile.sdk.reactnative.handlers.customregistration.CustomRegistrationAction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomRegistrationActionManager @Inject constructor() {

  private val customRegistrationActions = ArrayList<CustomRegistrationAction>()

  fun getCustomRegistrationAction(id: String?): CustomRegistrationAction? {
    return customRegistrationActions.firstOrNull { it.id == id }
  }

  fun getCustomRegistrationActions(): ArrayList<CustomRegistrationAction> {
    return customRegistrationActions
  }

  fun addCustomRegistrationAction(action: CustomRegistrationAction) {
    customRegistrationActions.add(action)
  }

  fun getActiveCustomRegistrationAction(): CustomRegistrationAction? {
    return customRegistrationActions.firstOrNull { it.isInProgress() }
  }
}
