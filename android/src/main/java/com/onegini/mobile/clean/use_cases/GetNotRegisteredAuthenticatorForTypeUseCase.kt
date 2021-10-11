package com.onegini.mobile.clean.use_cases

import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.exception.OneginiWrapperErrorException
import com.onegini.mobile.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.android.model.OneginiAuthenticator

class GetNotRegisteredAuthenticatorForTypeUseCase(private val oneginiSDK: OneginiSDK, private val getUserProfileUseCase: GetUserProfileUseCase = GetUserProfileUseCase(oneginiSDK)) {

    @Throws(OneginiWrapperErrorException::class)
    operator fun invoke(profileId: String, type: Int): OneginiAuthenticator {
        val userProfile = getUserProfileUseCase(profileId) ?: throw OneginiWrapperErrorException(OneginiWrapperErrors.USER_PROFILE_IS_NULL)

        var authenticator: OneginiAuthenticator? = null

        val notRegisteredAuthenticators: Set<OneginiAuthenticator> = oneginiSDK.oneginiClient.userClient.getNotRegisteredAuthenticators(userProfile)
        for (auth in notRegisteredAuthenticators) {
            if (auth.type == type) {
                authenticator = auth
            }
        }

        if (authenticator == null) {
            throw OneginiWrapperErrorException(OneginiWrapperErrors.AUTHENTICATED_USER_PROFILE_IS_NULL)
        }

        return authenticator
    }
}
