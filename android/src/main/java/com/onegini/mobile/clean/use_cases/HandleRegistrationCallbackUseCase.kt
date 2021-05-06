package com.onegini.mobile.clean.use_cases

import android.net.Uri
import com.onegini.mobile.OneginiComponets

class HandleRegistrationCallbackUseCase {

    operator fun invoke(uriString: String?) {
        val uri = Uri.parse(uriString)
        OneginiComponets.oneginiSDK.registrationRequestHandler.handleRegistrationCallback(uri)
    }
}
