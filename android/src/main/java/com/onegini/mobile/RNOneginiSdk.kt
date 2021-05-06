package com.onegini.mobile

import android.util.Log
import com.facebook.react.bridge.*
import com.onegini.mobile.Constants.PinFlow
import com.onegini.mobile.OneginiComponets.init
import com.onegini.mobile.clean.wrapper.IOneginiSdkWrapper
import com.onegini.mobile.clean.wrapper.OneginiSdkWrapper
import com.onegini.mobile.exception.OneginReactNativeException.Companion.FINGERPRINT_IS_NOT_ENABLED
import com.onegini.mobile.exception.OneginReactNativeException.Companion.MOBILE_AUTH_OTP_IS_DISABLED
import com.onegini.mobile.managers.RegistrationManager
import com.onegini.mobile.mapers.*
import com.onegini.mobile.sdk.android.handlers.*
import com.onegini.mobile.sdk.android.handlers.error.*
import com.onegini.mobile.view.handlers.pins.ChangePinHandler

class RNOneginiSdk(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext), IOneginiSdkWrapper {

    companion object {
        private const val LOG_TAG = "RNOneginiSdk"
        private const val TAG = "RNOneginiSdk"
    }

    private val sdkWrapper: OneginiSdkWrapper

    private val reactContext: ReactApplicationContext
    private val registrationManager: RegistrationManager

    private val oneginiSDK: OneginiSDK
        private get() = OneginiComponets.oneginiSDK

    init {
        init(reactContext.applicationContext)

        sdkWrapper = OneginiSdkWrapper(oneginiSDK, reactApplicationContext)

        this.reactContext = reactContext
        registrationManager = RegistrationManager(oneginiSDK)
    }

    override fun canOverrideExistingModule(): Boolean {
        return true
    }

    override fun getName(): String {
        return "RNOneginiSdk"
    }

    @ReactMethod
    override fun startClient(rnConfig: ReadableMap, promise: Promise) {
        sdkWrapper.startClient(rnConfig, promise)
    }

    @ReactMethod
    override fun getIdentityProviders(promise: Promise) {
        sdkWrapper.getIdentityProviders(promise)
    }

    @ReactMethod
    override fun getAccessToken(promise: Promise) {
        sdkWrapper.getAccessToken(promise)
    }

    @ReactMethod
    override fun getAuthenticatedUserProfile(promise: Promise) {
        sdkWrapper.getAuthenticatedUserProfile(promise)
    }

    @ReactMethod
    override fun getAllAuthenticators(profileId: String, promise: Promise) {
        sdkWrapper.getAllAuthenticators(profileId, promise)
    }

    @ReactMethod
    override fun getRegisteredAuthenticators(profileId: String, promise: Promise) {
        sdkWrapper.getRegisteredAuthenticators(profileId, promise)
    }

    @ReactMethod
    override fun registerAuthenticator(profileId: String, type: String, promise: Promise) {
        sdkWrapper.registerAuthenticatorUseCase(profileId, type, promise)
    }

    @ReactMethod
    override fun isAuthenticatorRegistered(profileId: String, type: String, promise: Promise) {
        sdkWrapper.isAuthenticatorRegistered(profileId, type, promise)
    }

    @ReactMethod
    override fun deregisterAuthenticator(profileId: String, type: String, promise: Promise) {
        sdkWrapper.deregisterAuthenticator(profileId, type, promise)
    }

    // TODO: temporary not to change RN SDK
    @ReactMethod
    fun registerFingerprintAuthenticator(profileId: String, promise: Promise) {
        sdkWrapper.registerAuthenticatorUseCase(profileId, "Fingerprint", promise)
    }

    // TODO: temporary not to change RN SDK
    @ReactMethod
    fun isFingerprintAuthenticatorRegistered(profileId: String, promise: Promise) {
        sdkWrapper.isAuthenticatorRegistered(profileId, "Fingerprint", promise)
    }

    // TODO: temporary not to change RN SDK
    @ReactMethod
    fun deregisterFingerprintAuthenticator(profileId: String, promise: Promise) {
        sdkWrapper.deregisterAuthenticator(profileId, "Fingerprint", promise)
    }

    @ReactMethod
    override fun submitFingerprintAcceptAuthenticationRequest(promise: Promise) {
        if (oneginiSDK.fingerprintAuthenticationRequestHandler == null) {
            promise.reject(FINGERPRINT_IS_NOT_ENABLED.toString(), " The fingerprint is no enabled. Please check your configuration")
        }
        oneginiSDK.fingerprintAuthenticationRequestHandler!!.acceptAuthenticationRequest()
    }

    @ReactMethod
    override fun submitFingerprintDenyAuthenticationRequest(promise: Promise) {
        if (oneginiSDK.fingerprintAuthenticationRequestHandler == null) {
            promise.reject(FINGERPRINT_IS_NOT_ENABLED.toString(), " The fingerprint is no enabled. Please check your configuration")
        }
        oneginiSDK.fingerprintAuthenticationRequestHandler!!.denyAuthenticationRequest()
    }

    @ReactMethod
    override fun submitFingerprintFallbackToPin(promise: Promise) {
        if (oneginiSDK.fingerprintAuthenticationRequestHandler == null) {
            promise.reject(FINGERPRINT_IS_NOT_ENABLED.toString(), " The fingerprint is no enabled. Please check your configuration")
        }
        oneginiSDK.fingerprintAuthenticationRequestHandler!!.fallbackToPin()
    }

    @ReactMethod
    override fun setPreferredAuthenticator(profileId: String, idOneginiAuthenticator: String, promise: Promise) {
        sdkWrapper.setPreferredAuthenticator(profileId, idOneginiAuthenticator, promise)
    }

    @ReactMethod
    override fun registerUser(identityProviderId: String?, promise: Promise) {
        sdkWrapper.registerUser(identityProviderId, promise)
    }

    @ReactMethod
    override fun deregisterUser(profileId: String?, promise: Promise) {
        sdkWrapper.deregisterUser(profileId, promise)
    }

    @ReactMethod
    override fun startSingleSignOn(url: String, promise: Promise) {
        sdkWrapper.startSingleSignOn(url, promise)
    }

    @ReactMethod
    override fun submitCustomRegistrationAction(customAction: String, identityProviderId: String, token: String?) {
        val action = registrationManager.getSimpleCustomRegistrationAction(identityProviderId)

        if (action == null) {
            Log.e(LOG_TAG, "The $identityProviderId was not configured.")
            return
        }

        when (customAction) {
            Constants.CUSTOM_REGISTRATION_ACTION_PROVIDE -> action.returnSuccess(token)
            Constants.CUSTOM_REGISTRATION_ACTION_CANCEL -> action.returnError(java.lang.Exception(token))
            else -> {
                Log.e(LOG_TAG, "Got unsupported custom registration action: $customAction.")
            }
        }
    }

    @ReactMethod
    override fun getRedirectUri(promise: Promise) {
        sdkWrapper.getRedirectUri(promise)
    }

    @ReactMethod
    override fun handleRegistrationCallback(uri: String?) {
        registrationManager.handleRegistrationCallback(uri)
    }

    @ReactMethod
    override fun cancelRegistration() {
        registrationManager.cancelRegistration()
    }

    @ReactMethod
    override fun changePin(promise: Promise) {
        oneginiSDK.changePinHandler.onStartChangePin(object : ChangePinHandler.ChangePinHandlerResponse {
            override fun onSuccess() {
                promise.resolve(null)
            }

            override fun onError(error: OneginiChangePinError?) {
                promise.reject(error?.errorType.toString(), error?.message)
            }
        })
    }

    @ReactMethod
    @Throws(Exception::class)
    override fun submitPinAction(flowString: String?, action: String, pin: String?) {
        val flow = PinFlow.parse(flowString)
        when (flow) {
            PinFlow.Authentication -> {
                submitAuthenticationPinAction(action, pin)
                return
            }
            PinFlow.Create -> {
                submitCreatePinAction(action, pin)
                return
            }
            PinFlow.Change -> {
                submitChangePinAction(action, pin)
                return
            }
        }
    }

    private fun submitCreatePinAction(action: String, pin: String?) {
        when (action) {
            Constants.PIN_ACTION_PROVIDE -> oneginiSDK.createPinRequestHandler.onPinProvided(pin!!.toCharArray(), PinFlow.Create)
            Constants.PIN_ACTION_CANCEL -> oneginiSDK.createPinRequestHandler.pinCancelled(PinFlow.Create)
            else -> Log.e(LOG_TAG, "Got unsupported PIN action: $action")
        }
    }

    private fun submitAuthenticationPinAction(action: String, pin: String?) {
        when (action) {
            Constants.PIN_ACTION_PROVIDE -> oneginiSDK.pinAuthenticationRequestHandler.acceptAuthenticationRequest(pin!!.toCharArray())
            Constants.PIN_ACTION_CANCEL -> oneginiSDK.pinAuthenticationRequestHandler.denyAuthenticationRequest()
            else -> Log.e(LOG_TAG, "Got unsupported PIN action: $action")
        }
    }

    private fun submitChangePinAction(action: String, pin: String?) {
        when (action) {
            Constants.PIN_ACTION_PROVIDE -> oneginiSDK.changePinHandler.onPinProvided(pin!!.toCharArray())
            Constants.PIN_ACTION_CANCEL -> oneginiSDK.changePinHandler.pinCancelled()
            else -> Log.e(LOG_TAG, "Got unsupported PIN action: $action")
        }
    }

    @ReactMethod
    override fun enrollMobileAuthentication(promise: Promise) {
        sdkWrapper.enrollMobileAuthentication(promise)
    }

    @ReactMethod
    override fun acceptMobileAuthConfirmation(promise: Promise) {
        val handler = oneginiSDK.mobileAuthOtpRequestHandler
        if (handler == null) {
            promise.reject(MOBILE_AUTH_OTP_IS_DISABLED.toString(), "The Mobile auth Otp is disabled")
        }
        handler!!.acceptAuthenticationRequest()
    }

    @ReactMethod
    override fun denyMobileAuthConfirmation(promise: Promise) {
        val handler = oneginiSDK.mobileAuthOtpRequestHandler
        if (handler == null) {
            promise.reject(MOBILE_AUTH_OTP_IS_DISABLED.toString(), "The Mobile auth Otp is disabled")
        }
        handler!!.denyAuthenticationRequest()
    }

    @ReactMethod
    override fun handleMobileAuthWithOtp(otpCode: String, promise: Promise) {
        sdkWrapper.handleMobileAuthWithOtp(otpCode, promise)
    }

    @ReactMethod
    override fun getUserProfiles(promise: Promise) {
        sdkWrapper.getUserProfiles(promise)
    }

    @ReactMethod
    override fun logout(promise: Promise) {
        sdkWrapper.logout(promise)
    }

    @ReactMethod
    override fun authenticateUser(profileId: String?, promise: Promise) {
        sdkWrapper.authenticateUser(profileId, promise)
    }

    @ReactMethod
    override fun authenticateUserImplicitly(profileId: String?, promise: Promise) {
        sdkWrapper.authenticateUserImplicitly(profileId, promise)
    }

    @ReactMethod
    override fun authenticateDeviceForResource(resourcePath: String, promise: Promise) {
        sdkWrapper.authenticateDeviceForResource(resourcePath, promise)
    }

    @ReactMethod
    override fun resourceRequest(type: String, details: ReadableMap, promise: Promise) {
        sdkWrapper.resourceRequest(type, details, promise)
    }

    //

    override fun onCatalystInstanceDestroy() {
        sdkWrapper.clear()

        super.onCatalystInstanceDestroy()
    }
}
