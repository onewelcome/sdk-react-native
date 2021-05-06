package com.onegini.mobile.clean.use_cases

import com.onegini.mobile.Constants
import com.onegini.mobile.OneginiComponets
import com.onegini.mobile.view.handlers.customregistration.SimpleCustomRegistrationAction

class SubmitCustomRegistrationActionUseCase {

    operator fun invoke(customAction: String, identityProviderId: String, token: String?) {
        val action = getSimpleCustomRegistrationAction(identityProviderId)

        when (customAction) {
            Constants.CUSTOM_REGISTRATION_ACTION_PROVIDE -> action?.returnSuccess(token)
            Constants.CUSTOM_REGISTRATION_ACTION_CANCEL -> action?.returnError(java.lang.Exception(token))
        }
    }

    //

    private fun getSimpleCustomRegistrationAction(id: String?): SimpleCustomRegistrationAction? {
        for (action in OneginiComponets.oneginiSDK.simpleCustomRegistrationActions) {
            if (action.getIdProvider() == id) {
                return action
            }
        }
        return null
    }
}
