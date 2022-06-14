package com.onegini.mobile.sdk.reactnative.mapers

import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.onegini.mobile.sdk.reactnative.model.rn.OneginiReactNativeConfig
import com.onegini.mobile.sdk.reactnative.model.rn.ReactNativeIdentityProvider

object OneginiReactNativeConfigMapper {

    @Throws(NullPointerException::class)
    fun toOneginiReactNativeConfig(rnConfig: ReadableMap): OneginiReactNativeConfig {
        return OneginiReactNativeConfig(
            rnConfig.getString("configModelClassName"),
            rnConfig.getString("securityControllerClassName"),
            toReactNativeIdentityProviderList(rnConfig.getArray("customProviders")),
            rnConfig.getBoolean("enableMobileAuthenticationOtp"),
            rnConfig.getBoolean("enableFingerprint")
        )
    }

    fun toReactNativeIdentityProviderList(identityProvider: ReadableArray?): List<ReactNativeIdentityProvider> {
        val list = mutableListOf<ReactNativeIdentityProvider>()
        identityProvider?.toArrayList()?.forEach {
            list.add(toReactNativeIdentityProvider(it as HashMap<String, *>))
        }
        return list
    }

    fun toReactNativeIdentityProvider(config: HashMap<String, *>): ReactNativeIdentityProvider {
        return ReactNativeIdentityProvider(config["id"]!! as String, config["isTwoStep"] as Boolean)
    }
}
