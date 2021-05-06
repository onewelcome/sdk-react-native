package com.onegini.mobile.clean.use_cases

import com.onegini.mobile.OneginiComponets

class SubmitFingerprintFallbackToPinUseCase {

    operator fun invoke() {
        OneginiComponets.oneginiSDK.fingerprintAuthenticationRequestHandler?.fallbackToPin()
    }
}
