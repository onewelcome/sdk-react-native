// @todo Later will be transferred to RN Wrapper
package com.onegini.mobile.sdk.reactnative

import android.content.Context
import com.onegini.mobile.sdk.android.client.OneginiClient
import com.onegini.mobile.sdk.android.client.OneginiClientBuilder
import com.onegini.mobile.sdk.android.model.OneginiClientConfigModel
import com.onegini.mobile.sdk.reactnative.handlers.RegistrationRequestHandler
import com.onegini.mobile.sdk.reactnative.handlers.customregistration.CustomRegistrationObserver
import com.onegini.mobile.sdk.reactnative.handlers.customregistration.SimpleCustomRegistrationAction
import com.onegini.mobile.sdk.reactnative.handlers.customregistration.SimpleCustomRegistrationFactory
import com.onegini.mobile.sdk.reactnative.handlers.fingerprint.FingerprintAuthenticationObserver
import com.onegini.mobile.sdk.reactnative.handlers.fingerprint.FingerprintAuthenticationRequestHandler
import com.onegini.mobile.sdk.reactnative.handlers.mobileauthotp.MobileAuthOtpRequestHandler
import com.onegini.mobile.sdk.reactnative.handlers.mobileauthotp.MobileAuthOtpRequestObserver
import com.onegini.mobile.sdk.reactnative.handlers.pins.ChangePinHandler
import com.onegini.mobile.sdk.reactnative.handlers.pins.CreatePinRequestHandler
import com.onegini.mobile.sdk.reactnative.handlers.pins.PinAuthenticationRequestHandler
import com.onegini.mobile.sdk.reactnative.handlers.pins.PinNotificationObserver
import com.onegini.mobile.sdk.reactnative.model.rn.OneginiReactNativeConfig
import java.util.concurrent.TimeUnit

class OneginiSDK(private val appContext: Context) {

    lateinit var registrationRequestHandler: RegistrationRequestHandler
        private set
    lateinit var pinAuthenticationRequestHandler: PinAuthenticationRequestHandler
        private set
    lateinit var createPinRequestHandler: CreatePinRequestHandler
        private set
    lateinit var changePinHandler: ChangePinHandler

    val simpleCustomRegistrationActions = ArrayList<SimpleCustomRegistrationAction>()

    var mobileAuthOtpRequestHandler: MobileAuthOtpRequestHandler? = null
        private set

    var fingerprintAuthenticationRequestHandler: FingerprintAuthenticationRequestHandler? = null
        private set

    private lateinit var config: OneginiReactNativeConfig

    fun init(oneginiReactNativeConfig: OneginiReactNativeConfig) {
        this.config = oneginiReactNativeConfig

        buildSDK(appContext)
    }

    val oneginiClient: OneginiClient
        get() {
            return OneginiClient.getInstance()!!
        }

    private fun buildSDK(context: Context): OneginiClient {
        val applicationContext = context.applicationContext
        registrationRequestHandler = RegistrationRequestHandler(applicationContext)
        pinAuthenticationRequestHandler = PinAuthenticationRequestHandler(this)
        createPinRequestHandler = CreatePinRequestHandler()
        changePinHandler = ChangePinHandler(this)

        // twoWayOtpIdentityProvider = TwoWayOtpIdentityProvider(context)
        val clientBuilder = OneginiClientBuilder(applicationContext, createPinRequestHandler, pinAuthenticationRequestHandler) // handlers for optional functionalities
            .setBrowserRegistrationRequestHandler(registrationRequestHandler) // Set http connect / read timeout
            .setHttpConnectTimeout(TimeUnit.SECONDS.toMillis(5).toInt())
            .setHttpReadTimeout(TimeUnit.SECONDS.toMillis(20).toInt())

        setProviders(clientBuilder)

        // Set config model
        setConfigModel(clientBuilder)

        // Set security controller
        setSecurityController(clientBuilder)

        if (config.enableMobileAuthenticationOtp) {
            mobileAuthOtpRequestHandler = MobileAuthOtpRequestHandler()
            clientBuilder.setMobileAuthWithOtpRequestHandler(mobileAuthOtpRequestHandler!!)
        }

        if (config.enableFingerprint) {
            fingerprintAuthenticationRequestHandler = FingerprintAuthenticationRequestHandler()
            clientBuilder.setFingerprintAuthenticationRequestHandler(fingerprintAuthenticationRequestHandler!!)
        }

        return clientBuilder.build()
    }

    private fun setProviders(clientBuilder: OneginiClientBuilder) {
        config.identityProviders.forEach {
            val provider = SimpleCustomRegistrationFactory.getSimpleCustomRegistrationProvider(it)
            simpleCustomRegistrationActions.add(provider.action)
            clientBuilder.addCustomIdentityProvider(provider)
        }
    }

    fun setPinNotificationObserver(pinNotificationObserver: PinNotificationObserver) {
        createPinRequestHandler.setPinNotificationObserver(pinNotificationObserver)
        pinAuthenticationRequestHandler.setPinNotificationObserver(pinNotificationObserver)
        changePinHandler.pinNotificationObserver = pinNotificationObserver
    }

    fun setCustomRegistrationObserver(observer: CustomRegistrationObserver) {
        simpleCustomRegistrationActions.forEach {
            it.setCustomRegistrationObserver(observer)
        }
    }

    fun setMobileAuthOtpRequestObserver(mobileAuthOtpRequestObserver: MobileAuthOtpRequestObserver) {
        mobileAuthOtpRequestHandler?.observer = mobileAuthOtpRequestObserver
    }

    fun setFingerprintAuthenticationObserver(observer: FingerprintAuthenticationObserver) {
        fingerprintAuthenticationRequestHandler?.observer = observer
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
