package com.onegini.mobile.clean.wrapper

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.clean.use_cases.GetAccessTokenUseCase
import com.onegini.mobile.clean.use_cases.GetIdentityProvidersUseCase
import com.onegini.mobile.clean.use_cases.StartClientUseCase

class OneginiSdkWrapper(
        private val oneginiSDK: OneginiSDK,
        private val reactApplicationContext: ReactApplicationContext,
        val startClientUseCase: StartClientUseCase = StartClientUseCase(oneginiSDK, reactApplicationContext),
        val getIdentityProvidersUseCase: GetIdentityProvidersUseCase = GetIdentityProvidersUseCase(oneginiSDK),
        val getAccessTokenUseCase: GetAccessTokenUseCase = GetAccessTokenUseCase(oneginiSDK)
): IOneginiSdkWrapper {

    //
    // Configuration
    //

    override fun startClient(rnConfig: ReadableMap, promise: Promise) {
        startClientUseCase(rnConfig, promise)
    }

    //
    // Session
    //

    override fun getIdentityProviders(promise: Promise) {
        getIdentityProvidersUseCase(promise)
    }

    override fun getAccessToken(promise: Promise) {
        getAccessTokenUseCase(promise)
    }

    override fun getAuthenticatedUserProfile(promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun getAllAuthenticators(profileId: String, promise: Promise) {
        TODO("Not yet implemented")
    }

    override fun getRegisteredAuthenticators(profileId: String, promise: Promise) {
        TODO("Not yet implemented")
    }

    // TODO: all other methods
}