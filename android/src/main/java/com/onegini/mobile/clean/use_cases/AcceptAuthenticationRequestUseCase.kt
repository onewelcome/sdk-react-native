package com.onegini.mobile.clean.use_cases

import com.onegini.mobile.OneginiComponets

class AcceptAuthenticationRequestUseCase {

    operator fun invoke(type: String, value: String?) {
        when (type) {
            "Fingerprint" -> OneginiComponets.oneginiSDK.fingerprintAuthenticationRequestHandler?.acceptAuthenticationRequest()
            "MobileAuthOtp" -> OneginiComponets.oneginiSDK.mobileAuthOtpRequestHandler?.acceptAuthenticationRequest()
            "Pin" -> value?.let { OneginiComponets.oneginiSDK.pinAuthenticationRequestHandler.acceptAuthenticationRequest(it.toCharArray()) }
        }
    }
}
