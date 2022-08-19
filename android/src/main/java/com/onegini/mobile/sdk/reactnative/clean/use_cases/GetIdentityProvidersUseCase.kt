package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.mapers.OneginiIdentityProviderMapper

class GetIdentityProvidersUseCase(private val oneginiSDK: OneginiSDK) {

    operator fun invoke(promise: Promise) {
        val providers = oneginiSDK.oneginiClient.userClient.identityProviders
        promise.resolve(OneginiIdentityProviderMapper.toWritableMap(providers))
    }
}
