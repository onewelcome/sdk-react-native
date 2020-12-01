package com.onegini.mobile.mapers

import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.onegini.mobile.model.rn.OneginiReactNativeConfig
import com.onegini.mobile.model.rn.ReactNativeIdentityProvider

object OneginiReactNativeConfigMapper {

    fun toOneginiReactNativeConfig(rnConfig: ReadableMap): OneginiReactNativeConfig {
        return OneginiReactNativeConfig(toReactNativeIdentityProviderList(rnConfig.getArray("customProviders")))
    }

    fun toReactNativeIdentityProviderList(identityProvider: ReadableArray?): List<ReactNativeIdentityProvider> {
        val list = mutableListOf<ReactNativeIdentityProvider>()
        identityProvider?.toArrayList()?.forEach {
            list.add(toReactNativeIdentityProvider(it as HashMap<String,*>))
        }
        return list
    }

    fun toReactNativeIdentityProvider(config: HashMap<String,*>): ReactNativeIdentityProvider {
        return ReactNativeIdentityProvider(config["id"]!! as String, config["isTwoStep"] as Boolean)
    }
}