package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.reactnative.mapers.OneginiAuthenticatorMapper
import com.onegini.mobile.sdk.android.model.OneginiAuthenticator
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRegisteredAuthenticatorsUseCase @Inject constructor(
    private val oneginiSDK: OneginiSDK,
    private val getUserProfileUseCase: GetUserProfileUseCase
) {
    operator fun invoke(profileId: String, promise: Promise) {
        val userProfile = getUserProfileUseCase(profileId)

        if (userProfile == null) {
            promise.reject(OneginiWrapperErrors.PROFILE_DOES_NOT_EXIST.code.toString(), OneginiWrapperErrors.PROFILE_DOES_NOT_EXIST.message)
            return
        }

        val authenticators = getList(userProfile)

        promise.resolve(OneginiAuthenticatorMapper.toWritableMap(authenticators))
    }

    fun getList(userProfile: UserProfile): Set<OneginiAuthenticator> {
        return oneginiSDK.oneginiClient.userClient.getRegisteredAuthenticators(userProfile)
    }
}
