package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.exception.OneginiWrapperErrorException
import com.onegini.mobile.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.android.model.OneginiAuthenticator

class IsAuthenticatorRegisteredUseCase(private val oneginiSDK: OneginiSDK, private val getNotRegisteredAuthenticatorForTypeUseCase: GetNotRegisteredAuthenticatorForTypeUseCase = GetNotRegisteredAuthenticatorForTypeUseCase(oneginiSDK)) {

    operator fun invoke(profileId: String, type: String, promise: Promise) {
        val authType = when (type) {
            "Fingerprint" -> OneginiAuthenticator.FINGERPRINT
            "Pin" -> OneginiAuthenticator.PIN
            "Custom" -> OneginiAuthenticator.CUSTOM
            else -> OneginiAuthenticator.CUSTOM
        }

        try {
            getNotRegisteredAuthenticatorForTypeUseCase(profileId, authType)
            promise.resolve(true)
        } catch (e: OneginiWrapperErrorException) {
            if (e.wrapperError == OneginiWrapperErrors.AUTHENTICATED_USER_PROFILE_IS_NULL) {
                promise.resolve(false)
            } else {
                promise.reject(e.wrapperError.code, e.wrapperError.message)
            }
        }
    }
}
