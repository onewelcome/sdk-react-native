package com.onegini.mobile.sdk.reactnative

import android.content.Context
import android.util.Log
import com.onegini.mobile.sdk.android.client.OneginiClient
import com.onegini.mobile.sdk.android.client.OneginiClientBuilder
import com.onegini.mobile.sdk.reactnative.handlers.customregistration.SimpleCustomRegistrationAction
import com.onegini.mobile.sdk.reactnative.handlers.customregistration.SimpleCustomRegistrationFactory
import com.onegini.mobile.sdk.reactnative.handlers.fingerprint.FingerprintAuthenticationRequestHandler
import com.onegini.mobile.sdk.reactnative.handlers.mobileauthotp.MobileAuthOtpRequestHandler
import com.onegini.mobile.sdk.reactnative.handlers.pins.CreatePinRequestHandler
import com.onegini.mobile.sdk.reactnative.handlers.pins.PinAuthenticationRequestHandler
import com.onegini.mobile.sdk.reactnative.handlers.registration.RegistrationRequestHandler
import com.onegini.mobile.sdk.reactnative.model.rn.OneginiReactNativeConfig
import com.onegini.mobile.sdk.reactnative.model.rn.ReactNativeIdentityProvider
import com.onegini.mobile.sdk.reactnative.utils.ClassLoader

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OneginiSDK @Inject constructor(
    private val applicationContext: Context,
    private val registrationRequestHandler: RegistrationRequestHandler,
    private val pinAuthenticationRequestHandler: PinAuthenticationRequestHandler,
    private val createPinRequestHandler: CreatePinRequestHandler,
    private val mobileAuthOtpRequestHandler: MobileAuthOtpRequestHandler,
    private val fingerprintAuthenticationRequestHandler: FingerprintAuthenticationRequestHandler,
    private val simpleCustomRegistrationFactory: SimpleCustomRegistrationFactory,
) {

    val simpleCustomRegistrationActions = ArrayList<SimpleCustomRegistrationAction>()

    val oneginiClient: OneginiClient
        get() {
            return OneginiClient.getInstance() ?: buildSDK(applicationContext)
        }

    private fun buildSDK(context: Context): OneginiClient {
        val applicationContext = context.applicationContext

        val clientBuilder = OneginiClientBuilder(applicationContext, createPinRequestHandler, pinAuthenticationRequestHandler)

        clientBuilder.setBrowserRegistrationRequestHandler(registrationRequestHandler)
            .setMobileAuthWithOtpRequestHandler(mobileAuthOtpRequestHandler)
            .setFingerprintAuthenticationRequestHandler(fingerprintAuthenticationRequestHandler)
            .setHttpConnectTimeout(Constants.httpConnectTimeoutBrowserRegistrationMiliseconds)
            .setHttpReadTimeout(Constants.httpReadTimeoutBrowserRegistrationMiliseconds)

        val identityProvider = loadIdentityProvidersFromConfig(context)
        addIdentityProviders(identityProvider, clientBuilder)
        return clientBuilder.build()
    }

    private fun loadIdentityProvidersFromConfig(context: Context): List<ReactNativeIdentityProvider> {
        return try {
            val configClass = ClassLoader(context).getClassByName("ReactNativeConfig").newInstance() as OneginiReactNativeConfig
            configClass.getIdentityProviders()
        } catch (e: ClassNotFoundException) {
            Log.e("Loading of ReactNativeConfig failed.", e.toString())
            emptyList()
        } catch (e: InstantiationException) {
            Log.e("onegini", "Loading of ReactNativeConfig failed. $e")
            emptyList()
        }
    }

    private fun addIdentityProviders(identityProviders: List<ReactNativeIdentityProvider>, clientBuilder: OneginiClientBuilder) {
        identityProviders.forEach {
            val provider = simpleCustomRegistrationFactory.getSimpleCustomRegistrationProvider(it)
            simpleCustomRegistrationActions.add(provider.action)
            clientBuilder.addCustomIdentityProvider(provider)
        }
    }
}
