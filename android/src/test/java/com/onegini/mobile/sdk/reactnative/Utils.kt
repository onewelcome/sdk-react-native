package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.onegini.mobile.sdk.android.client.OneginiClient
import com.onegini.mobile.sdk.android.client.OneginiClientBuilder
import com.onegini.mobile.sdk.android.handlers.OneginiInitializationHandler
import io.mockk.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set

object Utils {

    fun createProvider(id: String, isTwoStep: Boolean): HashMap<String, *> {
        val map = HashMap<String, Any>()
        map["id"] = id
        map["isTwoStep"] = isTwoStep
        return map
    }

    fun startClient(rNOneginiSdk: RNOneginiSdk, oneginiClient: OneginiClient, arrayProviders: ArrayList<Any>?) {
        startClient(rNOneginiSdk, oneginiClient, arrayProviders, false)
    }

    fun startClient(rNOneginiSdk: RNOneginiSdk, oneginiClient: OneginiClient, arrayProviders: ArrayList<Any>?, enableFingerprint: Boolean) {
        val config = mockkClass(ReadableMap::class)

        if (arrayProviders != null) {
            val providers = mockkClass(ReadableArray::class)
            every { providers.toArrayList() } returns arrayProviders
            every { config.getArray("customProviders") } returns providers
        } else {
            every { config.getArray("customProviders") } returns null
        }

        every { config.getString("configModelClassName") } returns null
        every { config.getString("securityControllerClassName") } returns null
        every { config.getBoolean("enableMobileAuthenticationOtp") } returns false
        every { config.getBoolean("enableFingerprint") } returns enableFingerprint

        mockkStatic(OneginiClient::class)
        every { OneginiClient.getInstance() } returns oneginiClient

        mockkConstructor(OneginiClientBuilder::class)
        every { anyConstructed<OneginiClientBuilder>().build() } returns oneginiClient

        val oneginiInitializationHandlerSlot = slot<OneginiInitializationHandler>()
        every {
            oneginiClient.start(capture(oneginiInitializationHandlerSlot))
        } answers {}

        val promise = mockk<Promise>(relaxed = true)
        rNOneginiSdk.startClient(config, promise)
        oneginiInitializationHandlerSlot.captured.onSuccess(emptySet())
    }
}