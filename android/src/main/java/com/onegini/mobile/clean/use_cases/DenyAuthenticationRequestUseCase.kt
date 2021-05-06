package com.onegini.mobile.clean.use_cases

import com.onegini.mobile.OneginiComponets

class DenyAuthenticationRequestUseCase {

    operator fun invoke(type: String) {
        when (type) {
            "Fingerprint" -> OneginiComponets.oneginiSDK.fingerprintAuthenticationRequestHandler?.denyAuthenticationRequest()
            "MobileAuthOtp" -> OneginiComponets.oneginiSDK.mobileAuthOtpRequestHandler?.denyAuthenticationRequest()
            "Pin" -> OneginiComponets.oneginiSDK.pinAuthenticationRequestHandler.denyAuthenticationRequest()
        }
    }
}
