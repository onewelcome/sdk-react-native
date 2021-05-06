package com.onegini.mobile.clean.use_cases

import com.onegini.mobile.Constants
import com.onegini.mobile.OneginiComponets

class SubmitPinActionUseCase(
    val acceptAuthenticationRequestUseCase: AcceptAuthenticationRequestUseCase = AcceptAuthenticationRequestUseCase(),
    val denyAuthenticationRequestUseCase: DenyAuthenticationRequestUseCase = DenyAuthenticationRequestUseCase()
) {

    operator fun invoke(flowString: String?, action: String, pin: String?) {
        when (Constants.PinFlow.parse(flowString)) {
            Constants.PinFlow.Authentication -> {
                submitAuthenticationPinAction(action, pin)
                return
            }
            Constants.PinFlow.Create -> {
                submitCreatePinAction(action, pin)
                return
            }
            Constants.PinFlow.Change -> {
                submitChangePinAction(action, pin)
                return
            }
        }
    }

    //

    private fun submitAuthenticationPinAction(action: String, pin: String?) {
        when (action) {
            Constants.PIN_ACTION_PROVIDE -> acceptAuthenticationRequestUseCase("Pin", pin)
            Constants.PIN_ACTION_CANCEL -> denyAuthenticationRequestUseCase("Pin")
        }
    }

    private fun submitCreatePinAction(action: String, pin: String?) {
        when (action) {
            Constants.PIN_ACTION_PROVIDE -> OneginiComponets.oneginiSDK.createPinRequestHandler.onPinProvided(pin!!.toCharArray(), Constants.PinFlow.Create)
            Constants.PIN_ACTION_CANCEL -> OneginiComponets.oneginiSDK.createPinRequestHandler.pinCancelled(Constants.PinFlow.Create)
        }
    }

    private fun submitChangePinAction(action: String, pin: String?) {
        when (action) {
            Constants.PIN_ACTION_PROVIDE -> OneginiComponets.oneginiSDK.changePinHandler.onPinProvided(pin!!.toCharArray())
            Constants.PIN_ACTION_CANCEL -> OneginiComponets.oneginiSDK.changePinHandler.pinCancelled()
        }
    }
}
