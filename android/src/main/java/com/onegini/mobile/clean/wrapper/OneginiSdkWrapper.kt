package com.onegini.mobile.clean.wrapper

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import com.onegini.mobile.clean.use_cases.GetIdentityProvidersUseCase
import com.onegini.mobile.clean.use_cases.StartClientUseCase

class OneginiSdkWrapper(
        val startClientUseCase: StartClientUseCase = StartClientUseCase(),
        val getIdentityProvidersUseCase: GetIdentityProvidersUseCase = GetIdentityProvidersUseCase()
): IOneginiSdkWrapper {

    //
    // Configuration
    //

    override fun startClient(rnConfig: ReadableMap, promise: Promise, onSuccess: () -> Unit) {
        startClientUseCase(rnConfig) { success, error ->
            if (success) {
                promise.resolve(null)

                // TODO - handle logic in UseCase
                onSuccess()
            } else {
                error?.let {
                    promise.reject(it.type, it.message)
                }
            }
        }
    }

    //
    // Session
    //

    override fun getIdentityProviders(promise: Promise) {
        getIdentityProvidersUseCase() {
            promise.resolve(it)
        }
    }

    override fun getAccessToken(promise: Promise) {
        TODO("Not yet implemented")
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