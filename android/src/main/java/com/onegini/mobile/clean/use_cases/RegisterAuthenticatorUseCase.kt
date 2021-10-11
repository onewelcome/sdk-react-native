package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.exception.OneginiWrapperErrorException
import com.onegini.mobile.mapers.CustomInfoMapper
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticatorRegistrationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiAuthenticatorRegistrationError
import com.onegini.mobile.sdk.android.model.OneginiAuthenticator
import com.onegini.mobile.sdk.android.model.entity.CustomInfo

class RegisterAuthenticatorUseCase(private val oneginiSDK: OneginiSDK, private val getNotRegisteredAuthenticatorForTypeUseCase: GetNotRegisteredAuthenticatorForTypeUseCase = GetNotRegisteredAuthenticatorForTypeUseCase(oneginiSDK)) {

    operator fun invoke(profileId: String, type: String, promise: Promise) {
        val authType = when (type) {
            "Fingerprint" -> OneginiAuthenticator.FINGERPRINT
            "Pin" -> OneginiAuthenticator.PIN
            "Custom" -> OneginiAuthenticator.CUSTOM
            else -> OneginiAuthenticator.CUSTOM
        }

        try {
            var authenticator = getNotRegisteredAuthenticatorForTypeUseCase(profileId, authType)

            oneginiSDK.oneginiClient.userClient.registerAuthenticator(
                authenticator,
                object : OneginiAuthenticatorRegistrationHandler {
                    override fun onSuccess(info: CustomInfo?) {
                        promise.resolve(CustomInfoMapper.toWritableMap(info))
                    }

                    override fun onError(error: OneginiAuthenticatorRegistrationError?) {
                        promise.reject(error?.errorType.toString(), error?.message)
                    }
                }
            )
        } catch (e: OneginiWrapperErrorException) {
            promise.reject(e.wrapperError.code, e.wrapperError.message)
            return
        }
    }
}
