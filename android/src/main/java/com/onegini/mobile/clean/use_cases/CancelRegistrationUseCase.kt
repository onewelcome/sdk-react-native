package com.onegini.mobile.clean.use_cases

import com.onegini.mobile.OneginiComponets

class CancelRegistrationUseCase {

    operator fun invoke() {
        OneginiComponets.oneginiSDK.registrationRequestHandler.onRegistrationCanceled()
    }
}
