package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.Constants
import com.onegini.mobile.OneginiComponets
import com.onegini.mobile.exception.OneginiWrapperErrors
import com.onegini.mobile.mapers.UserProfileMapper
import com.onegini.mobile.sdk.android.handlers.OneginiRegistrationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiRegistrationError
import com.onegini.mobile.sdk.android.model.OneginiIdentityProvider
import com.onegini.mobile.sdk.android.model.entity.CustomInfo
import com.onegini.mobile.sdk.android.model.entity.UserProfile

//
// TODO: (?) adjust to new registration flow?
//
class RegisterUserUseCase {

    operator fun invoke(identityProviderId: String?, promise: Promise) {
        val identityProvider = getIdentityProvider(identityProviderId)

        if (identityProvider == null && identityProviderId != null) {
            promise.reject(OneginiWrapperErrors.IDENTITY_PROVIDER_NOT_FOUND.code, OneginiWrapperErrors.IDENTITY_PROVIDER_NOT_FOUND.message)
            return
        }

        // shouldn't it be passed as a param?
        val scopes = Constants.DEFAULT_SCOPES

        OneginiComponets.oneginiSDK.oneginiClient.userClient.registerUser(
            identityProvider, scopes,
            object : OneginiRegistrationHandler {
                override fun onSuccess(userProfile: UserProfile?, customInfo: CustomInfo?) {
                    promise.resolve(UserProfileMapper.toWritableMap(userProfile))
                }

                override fun onError(error: OneginiRegistrationError) {
                    promise.reject(error.errorType.toString(), error.message)
                }
            }
        )
    }

    //

    private fun getIdentityProvider(id: String?): OneginiIdentityProvider? {
        for (identity in OneginiComponets.oneginiSDK.oneginiClient.userClient.identityProviders) {
            if (identity.id == id) {
                return identity
            }
        }
        return null
    }
}
