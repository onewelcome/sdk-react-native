package com.onegini.mobile.clean.use_cases

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap
import com.onegini.mobile.OneginiComponets
import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.clean.model.SdkError
import com.onegini.mobile.mapers.OneginiIdentityProviderMapper

class GetIdentityProvidersUseCase {

    operator fun invoke(promise: Promise) {
        val providers = OneginiComponets.oneginiSDK.oneginiClient.userClient.identityProviders
        promise.resolve(OneginiIdentityProviderMapper.toWritableMap(providers))
    }
}