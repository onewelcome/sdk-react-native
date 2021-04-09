package com.onegini.mobile.clean.wrapper

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import com.onegini.mobile.clean.model.SdkError


interface IOneginiSdkWrapper {

    //
    // Configuration
    //

    fun startClient(rnConfig: ReadableMap, promise: Promise, onSuccess: () -> Unit)

    //
    // Session
    //
    
    fun getIdentityProviders(promise: Promise)

    fun getAccessToken(promise: Promise)

    fun getAuthenticatedUserProfile(promise: Promise)

    fun getAllAuthenticators(profileId: String, promise: Promise)

    fun getRegisteredAuthenticators(profileId: String, promise: Promise)


    // TODO: all other methods

}