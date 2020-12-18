//@todo Later will be transferred to RN Wrapper
package com.onegini.mobile

import android.content.Context
import com.onegini.mobile.model.rn.OneginiReactNativeConfig
import com.onegini.mobile.sdk.android.client.OneginiClient
import com.onegini.mobile.sdk.android.client.OneginiClientBuilder
import com.onegini.mobile.sdk.android.model.OneginiClientConfigModel
import com.onegini.mobile.view.handlers.*
import com.onegini.mobile.view.handlers.customregistration.CustomRegistrationObserver
import com.onegini.mobile.view.handlers.customregistration.SimpleCustomRegistrationAction
import com.onegini.mobile.view.handlers.customregistration.SimpleCustomRegistrationFactory
import com.onegini.mobile.view.handlers.fingerprint.FingerprintAuthenticationObserver
import com.onegini.mobile.view.handlers.fingerprint.FingerprintAuthenticationRequestHandler
import com.onegini.mobile.view.handlers.mobileauthotp.MobileAuthOtpRequestHandler
import com.onegini.mobile.view.handlers.mobileauthotp.MobileAuthOtpRequestObserver
import java.lang.reflect.InvocationTargetException
import java.util.*
import java.util.concurrent.TimeUnit

class OneginiSDK(private val appContext: Context) {

    var isInitialized: Boolean = false

    lateinit var registrationRequestHandler: RegistrationRequestHandler
        private set
    lateinit var pinAuthenticationRequestHandler: PinAuthenticationRequestHandler
        private set
    lateinit var createPinRequestHandler: CreatePinRequestHandler
        private set
    lateinit var changePinHandler: ChangePinHandler
        private set
    private val registeredHandlers = ArrayList<RegisteredHandler>()

    val simpleCustomRegistrationActions = ArrayList<SimpleCustomRegistrationAction>()

    var mobileAuthOtpRequestHandler: MobileAuthOtpRequestHandler? = null
        private set

    var fingerprintAuthenticationRequestHandler: FingerprintAuthenticationRequestHandler? = null
        private set

    lateinit var config: OneginiReactNativeConfig
        private set

    fun init(oneginiReactNativeConfig: OneginiReactNativeConfig) {
        this.config = oneginiReactNativeConfig
        buildSDK(appContext)
    }

    val oneginiClient: OneginiClient
        get() {
            var oneginiClient = OneginiClient.getInstance()
            if (oneginiClient == null) {
                oneginiClient = buildSDK(appContext)
            }
            return oneginiClient
        }

    private fun buildSDK(context: Context): OneginiClient {
        val applicationContext = context.applicationContext
        registrationRequestHandler = RegistrationRequestHandler(applicationContext)
        registeredHandlers.add(registrationRequestHandler)
        pinAuthenticationRequestHandler = PinAuthenticationRequestHandler(this)
        registeredHandlers.add(pinAuthenticationRequestHandler)
        createPinRequestHandler = CreatePinRequestHandler(applicationContext, this)
        registeredHandlers.add(createPinRequestHandler)
        changePinHandler = ChangePinHandler(this)
        registeredHandlers.add(changePinHandler)

        //twoWayOtpIdentityProvider = TwoWayOtpIdentityProvider(context)
        val clientBuilder = OneginiClientBuilder(applicationContext, createPinRequestHandler, pinAuthenticationRequestHandler)

        clientBuilder.setBrowserRegistrationRequestHandler(registrationRequestHandler) // Set http connect / read timeout
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

    fun onStart() {
        for (handler in registeredHandlers) {
            handler.onStart()
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
        try {
            val clazz = Class.forName(config.configModelClassName!!)
            val ctor = clazz.getConstructor()
            val `object` = ctor.newInstance()
            if (`object` is OneginiClientConfigModel) {
                clientBuilder.setConfigModel(`object`)
            }
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
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