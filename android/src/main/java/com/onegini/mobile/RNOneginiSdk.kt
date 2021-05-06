package com.onegini.mobile

import com.facebook.react.bridge.*
import com.onegini.mobile.Constants.PinFlow
import com.onegini.mobile.OneginiComponets.init
import com.onegini.mobile.clean.wrapper.IOneginiSdkWrapper
import com.onegini.mobile.clean.wrapper.OneginiSdkWrapper
import com.onegini.mobile.mapers.*
import com.onegini.mobile.sdk.android.handlers.*
import com.onegini.mobile.sdk.android.handlers.error.*
import com.onegini.mobile.view.handlers.pins.ChangePinHandler

class RNOneginiSdk(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext), IOneginiSdkWrapper {

    private val sdkWrapper: OneginiSdkWrapper

    private val oneginiSDK: OneginiSDK
        private get() = OneginiComponets.oneginiSDK

    init {
        init(reactContext.applicationContext)

        sdkWrapper = OneginiSdkWrapper(oneginiSDK, reactApplicationContext)
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
    override fun acceptAuthenticationRequest(type: String, value: String?) {
        sdkWrapper.acceptAuthenticationRequestUseCase(type, value)
    }

    @ReactMethod
    override fun denyAuthenticationRequest(type: String) {
        sdkWrapper.denyAuthenticationRequest(type)
    }

    // TODO: temporary not to change RN SDK
    @ReactMethod
    fun submitFingerprintAcceptAuthenticationRequest(promise: Promise) {
        sdkWrapper.acceptAuthenticationRequestUseCase("Fingerprint", null)
    }

    // TODO: temporary not to change RN SDK
    @ReactMethod
    fun acceptMobileAuthConfirmation(promise: Promise) {
        sdkWrapper.acceptAuthenticationRequestUseCase("MobileAuthOtp", null)
    }

    // TODO: temporary not to change RN SDK
    @ReactMethod
    fun denyMobileAuthConfirmation(promise: Promise) {
        sdkWrapper.denyAuthenticationRequestUseCase("MobileAuthOtp")
    }

    private fun submitAuthenticationPinAction(action: String, pin: String?) {
        when (action) {
            Constants.PIN_ACTION_PROVIDE -> sdkWrapper.acceptAuthenticationRequestUseCase("Pin", pin)
            Constants.PIN_ACTION_CANCEL -> sdkWrapper.denyAuthenticationRequestUseCase("Pin")
//            else -> Log.e(LOG_TAG, "Got unsupported PIN action: $action")
        }
    }

    // TODO: temporary not to change RN SDK
    @ReactMethod
    fun submitFingerprintDenyAuthenticationRequest(promise: Promise) {
        sdkWrapper.denyAuthenticationRequestUseCase("Fingerprint")
    }

    @ReactMethod
    override fun submitFingerprintFallbackToPin() {
        sdkWrapper.submitFingerprintFallbackToPin()
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
        sdkWrapper.submitCustomRegistrationAction(customAction, identityProviderId, token)
    }

    @ReactMethod
    override fun getRedirectUri(promise: Promise) {
        sdkWrapper.getRedirectUri(promise)
    }

    @ReactMethod
    override fun handleRegistrationCallback(uri: String?) {
        sdkWrapper.handleRegistrationCallback(uri)
    }

    @ReactMethod
    override fun cancelRegistration() {
        sdkWrapper.cancelRegistration()
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
//            else -> Log.e(LOG_TAG, "Got unsupported PIN action: $action")
        }
    }

    private fun submitChangePinAction(action: String, pin: String?) {
        when (action) {
            Constants.PIN_ACTION_PROVIDE -> oneginiSDK.changePinHandler.onPinProvided(pin!!.toCharArray())
            Constants.PIN_ACTION_CANCEL -> oneginiSDK.changePinHandler.pinCancelled()
//            else -> Log.e(LOG_TAG, "Got unsupported PIN action: $action")
        }
    }

    @ReactMethod
    override fun enrollMobileAuthentication(promise: Promise) {
        sdkWrapper.enrollMobileAuthentication(promise)
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
