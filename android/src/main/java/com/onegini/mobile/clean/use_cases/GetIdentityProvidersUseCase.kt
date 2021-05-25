package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.OneginiComponets
import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.mapers.OneginiIdentityProviderMapper

class GetIdentityProvidersUseCase(private val oneginiSDK: OneginiSDK) {

    operator fun invoke(promise: Promise) {
        val providers = oneginiSDK.oneginiClient.userClient.identityProviders
        promise.resolve(OneginiIdentityProviderMapper.toWritableMap(providers))
    }
}