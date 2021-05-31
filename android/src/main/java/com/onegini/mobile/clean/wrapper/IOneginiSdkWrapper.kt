package com.onegini.mobile.clean.wrapper

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap

interface IOneginiSdkWrapper {

    //
    // Configuration
    //

    fun startClient(rnConfig: ReadableMap, promise: Promise)

    //
    // Session
    //

    fun getIdentityProviders(promise: Promise)

    fun getAccessToken(promise: Promise)

    fun getAuthenticatedUserProfile(promise: Promise)

    fun getAllAuthenticators(profileId: String, promise: Promise)

    fun getRegisteredAuthenticators(profileId: String, promise: Promise)

    //

    fun registerUser(identityProviderId: String?, scopes: ReadableArray, promise: Promise)

    // TODO: all other methods
}
