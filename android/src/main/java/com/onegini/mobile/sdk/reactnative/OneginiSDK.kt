package com.onegini.mobile.sdk.reactnative

import android.content.Context
import com.facebook.react.bridge.ReactApplicationContext
import com.onegini.mobile.sdk.android.client.OneginiClient
import com.onegini.mobile.sdk.android.client.OneginiClientBuilder
import com.onegini.mobile.sdk.android.model.OneginiClientConfigModel
import com.onegini.mobile.sdk.reactnative.handlers.registration.RegistrationRequestHandler
import com.onegini.mobile.sdk.reactnative.handlers.customregistration.SimpleCustomRegistrationAction
import com.onegini.mobile.sdk.reactnative.handlers.customregistration.SimpleCustomRegistrationFactory
import com.onegini.mobile.sdk.reactnative.handlers.fingerprint.FingerprintAuthenticationRequestHandler
import com.onegini.mobile.sdk.reactnative.handlers.mobileauthotp.MobileAuthOtpRequestHandler
import com.onegini.mobile.sdk.reactnative.handlers.pins.CreatePinRequestHandler
import com.onegini.mobile.sdk.reactnative.handlers.pins.PinAuthenticationRequestHandler
import com.onegini.mobile.sdk.reactnative.model.rn.OneginiReactNativeConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OneginiSDK @Inject constructor(
    private val reactApplicationContext: ReactApplicationContext,
    val registrationRequestHandler: RegistrationRequestHandler,
    val pinAuthenticationRequestHandler: PinAuthenticationRequestHandler,
    val createPinRequestHandler: CreatePinRequestHandler,
    val mobileAuthOtpRequestHandler: MobileAuthOtpRequestHandler,
    val fingerprintAuthenticationRequestHandler: FingerprintAuthenticationRequestHandler,
    private val simpleCustomRegistrationFactory: SimpleCustomRegistrationFactory,
) {

    val simpleCustomRegistrationActions = ArrayList<SimpleCustomRegistrationAction>()

    lateinit var config: OneginiReactNativeConfig
        private set

    fun init(oneginiReactNativeConfig: OneginiReactNativeConfig) {
        this.config = oneginiReactNativeConfig

        buildSDK(reactApplicationContext.applicationContext)
    }

    val oneginiClient: OneginiClient
        get() {
            return OneginiClient.getInstance()!!
        }

    private fun buildSDK(context: Context): OneginiClient {
        val applicationContext = context.applicationContext

        val clientBuilder = OneginiClientBuilder(applicationContext, createPinRequestHandler, pinAuthenticationRequestHandler)

        clientBuilder.setBrowserRegistrationRequestHandler(registrationRequestHandler)
                .setHttpConnectTimeout(Constants.httpConnectTimeoutBrowserRegistrationMiliseconds)
                .setHttpReadTimeout(Constants.httpReadTimeoutBrowserRegistrationMiliseconds)

        setProviders(clientBuilder)
        setConfigModel(clientBuilder)
        setSecurityController(clientBuilder)

        if (config.enableMobileAuthenticationOtp) {
            clientBuilder.setMobileAuthWithOtpRequestHandler(mobileAuthOtpRequestHandler)
        }

        if (config.enableFingerprint) {
            clientBuilder.setFingerprintAuthenticationRequestHandler(fingerprintAuthenticationRequestHandler)
        }

        return clientBuilder.build()
    }

    private fun setProviders(clientBuilder: OneginiClientBuilder) {
        config.identityProviders.forEach {
            val provider = simpleCustomRegistrationFactory.getSimpleCustomRegistrationProvider(it)
            simpleCustomRegistrationActions.add(provider.action)
            clientBuilder.addCustomIdentityProvider(provider)
        }
    }

    private fun setConfigModel(clientBuilder: OneginiClientBuilder) {
        if (config.configModelClassName == null) {
            return
        }

        val clazz = Class.forName(config.configModelClassName!!)
        val ctor = clazz.getConstructor()
        val `object` = ctor.newInstance()
        if (`object` is OneginiClientConfigModel) {
            clientBuilder.setConfigModel(`object`)
        }
    }

    private fun setSecurityController(clientBuilder: OneginiClientBuilder) {
        if (config.securityControllerClassName == null) {
            return
        }
        try {
            val securityController = Class.forName(config.securityControllerClassName!!)
            clientBuilder.setSecurityController(securityController)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }
}
