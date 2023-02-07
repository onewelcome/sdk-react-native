package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.onegini.mobile.sdk.android.model.entity.CustomInfo
import com.onegini.mobile.sdk.reactnative.RNOneginiSdk.FunctionParams.AuthenticatorId
import com.onegini.mobile.sdk.reactnative.RNOneginiSdk.FunctionParams.Details
import com.onegini.mobile.sdk.reactnative.RNOneginiSdk.FunctionParams.IdentityProviderId
import com.onegini.mobile.sdk.reactnative.RNOneginiSdk.FunctionParams.Message
import com.onegini.mobile.sdk.reactnative.RNOneginiSdk.FunctionParams.OtpCode
import com.onegini.mobile.sdk.reactnative.RNOneginiSdk.FunctionParams.Pin
import com.onegini.mobile.sdk.reactnative.RNOneginiSdk.FunctionParams.ProfileId
import com.onegini.mobile.sdk.reactnative.RNOneginiSdk.FunctionParams.RnConfig
import com.onegini.mobile.sdk.reactnative.RNOneginiSdk.FunctionParams.Type
import com.onegini.mobile.sdk.reactnative.RNOneginiSdk.FunctionParams.Uri
import com.onegini.mobile.sdk.reactnative.clean.wrapper.OneginiSdkWrapper
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.reactnative.exception.PARAM_CAN_NOT_BE_NULL
import com.onegini.mobile.sdk.reactnative.handlers.fingerprint.FingerprintAuthenticationRequestHandler
import com.onegini.mobile.sdk.reactnative.handlers.mobileauthotp.MobileAuthOtpRequestHandler
import com.onegini.mobile.sdk.reactnative.handlers.pins.CreatePinRequestHandler
import com.onegini.mobile.sdk.reactnative.handlers.pins.PinAuthenticationRequestHandler
import com.onegini.mobile.sdk.reactnative.handlers.registration.RegistrationRequestHandler
import com.onegini.mobile.sdk.reactnative.managers.AuthenticatorManager
import com.onegini.mobile.sdk.reactnative.managers.AuthenticatorManager.DeregistrationCallback
import com.onegini.mobile.sdk.reactnative.managers.AuthenticatorManager.RegistrationCallback
import com.onegini.mobile.sdk.reactnative.managers.RegistrationManager
import com.onegini.mobile.sdk.reactnative.mapers.CustomInfoMapper
import com.onegini.mobile.sdk.reactnative.module.RNOneginiSdkModule
import javax.inject.Inject


class RNOneginiSdk(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    @Inject
    lateinit var oneginiSDK: OneginiSDK
    @Inject
    lateinit var registrationManager: RegistrationManager
    @Inject
    lateinit var authenticatorManager: AuthenticatorManager
    @Inject
    lateinit var sdkWrapper: OneginiSdkWrapper
    @Inject
    lateinit var fingerprintAuthenticationRequestHandler: FingerprintAuthenticationRequestHandler
    @Inject
    lateinit var registrationRequestHandler: RegistrationRequestHandler
    @Inject
    lateinit var mobileAuthOtpRequestHandler: MobileAuthOtpRequestHandler
    @Inject
    lateinit var createPinRequestHandler: CreatePinRequestHandler
    @Inject
    lateinit var pinAuthenticationRequestHandler: PinAuthenticationRequestHandler



    override fun initialize() {
        super.initialize()
        val component = DaggerRNOneginiSdkComponent.builder()
            .rNOneginiSdkModule(RNOneginiSdkModule(reactContext))
            .build()
        component.inject(this)
    }

    enum class FunctionParams(val paramName: String, val type: String ) {
        RnConfig("rnConfig", "ReadableMap"),
        ProfileId("profileId", "string"),
        AuthenticatorId("authenticatorId", "string"),
        Pin("pin", "string"),
        PinFlow("pinFlow", "string"),
        Uri("uri", "string"),
        IdentityProviderId("identityProviderId", "string"),
        OtpCode("otpCode", "string"),
        Type("type", "string"),
        Details("details", "string"),
        Message("message", "string"),
    }

    override fun canOverrideExistingModule(): Boolean {
        return true
    }

    override fun getName(): String {
        return "RNOneginiSdk"
    }

    private fun Promise.rejectWithNullError(paramName: String, paramType: String){
        this.reject(OneginiWrapperErrors.PARAMETERS_NOT_CORRECT.code.toString(), String.format(PARAM_CAN_NOT_BE_NULL, paramName, paramType))
    }

    @ReactMethod
    fun startClient(rnConfig: ReadableMap?, promise: Promise) {
        when (rnConfig) {
            null -> promise.rejectWithNullError(RnConfig.paramName, RnConfig.type)
            else -> sdkWrapper.startClient(rnConfig, promise)
        }
    }

    @ReactMethod
    fun getIdentityProviders(promise: Promise) {
        sdkWrapper.getIdentityProviders(promise)
    }

    @ReactMethod
    fun getAccessToken(promise: Promise) {
        sdkWrapper.getAccessToken(promise)
    }

    @ReactMethod
    fun getAuthenticatedUserProfile(promise: Promise) {
        sdkWrapper.getAuthenticatedUserProfile(promise)
    }

    @ReactMethod
    fun getAllAuthenticators(profileId: String?, promise: Promise) {
        when (profileId) {
            null -> promise.rejectWithNullError(ProfileId.paramName, ProfileId.type)
            else -> sdkWrapper.getAllAuthenticators(profileId, promise)
        }
    }

    @ReactMethod
    fun getRegisteredAuthenticators(profileId: String?, promise: Promise) {
        when (profileId) {
            null -> promise.rejectWithNullError(ProfileId.paramName, ProfileId.type)
            else -> sdkWrapper.getRegisteredAuthenticators(profileId, promise)
        }
    }

    @ReactMethod
    fun registerAuthenticator(authenticatorId: String?, promise: Promise) {
        when (authenticatorId) {
            null -> promise.rejectWithNullError(AuthenticatorId.paramName, AuthenticatorId.type)
            else -> sdkWrapper.registerAuthenticator(authenticatorId, promise)
        }
    }

    @ReactMethod
    fun registerFingerprintAuthenticator(profileId: String?, promise: Promise) {
        when (profileId) {
            null -> promise.rejectWithNullError(ProfileId.paramName, ProfileId.type)
            else -> {
                authenticatorManager.registerFingerprintAuthenticator(
                    profileId,
                    object : RegistrationCallback {
                        override fun onSuccess(customInfo: CustomInfo?) {
                            promise.resolve(CustomInfoMapper.toWritableMap(customInfo))
                        }
                        override fun onError(code: String?, message: String?) {
                            promise.reject(code, message)
                        }
                    }
                )
            }
        }
    }

    @ReactMethod
    fun deregisterFingerprintAuthenticator(profileId: String?, promise: Promise) {
        when (profileId) {
            null -> promise.rejectWithNullError(ProfileId.paramName, ProfileId.type)
            else -> {
                authenticatorManager.deregisterFingerprintAuthenticator(
                    profileId,
                    object : DeregistrationCallback {
                        override fun onSuccess() {
                            promise.resolve(null)
                        }
                        override fun onError(code: String?, message: String?) {
                            promise.reject(code, message)
                        }
                    }
                )
            }
        }
    }

    @ReactMethod
    fun submitFingerprintAcceptAuthenticationRequest(promise: Promise) {
        when (oneginiSDK.config.enableFingerprint) {
            false -> promise.reject(OneginiWrapperErrors.FINGERPRINT_IS_NOT_ENABLED.code.toString(), OneginiWrapperErrors.FINGERPRINT_IS_NOT_ENABLED.message)
            true -> {
                fingerprintAuthenticationRequestHandler.acceptAuthenticationRequest()
                promise.resolve(null)
            }
        }
    }

    @ReactMethod
    fun submitFingerprintDenyAuthenticationRequest(promise: Promise) {
        when (oneginiSDK.config.enableFingerprint) {
            false -> promise.reject(OneginiWrapperErrors.FINGERPRINT_IS_NOT_ENABLED.code.toString(), OneginiWrapperErrors.FINGERPRINT_IS_NOT_ENABLED.message)
            true -> {
                fingerprintAuthenticationRequestHandler.denyAuthenticationRequest()
                promise.resolve(null)
            }
        }
    }

    @ReactMethod
    fun submitFingerprintFallbackToPin(promise: Promise) {
        when (oneginiSDK.config.enableFingerprint) {
            false -> promise.reject(OneginiWrapperErrors.FINGERPRINT_IS_NOT_ENABLED.code.toString(), OneginiWrapperErrors.FINGERPRINT_IS_NOT_ENABLED.message)
            true -> {
                fingerprintAuthenticationRequestHandler.fallbackToPin()
                promise.resolve(null)
            }
        }
    }

    @ReactMethod
    fun setPreferredAuthenticator(authenticatorId: String?, promise: Promise) {
        when {
            authenticatorId == null -> promise.rejectWithNullError(AuthenticatorId.paramName, AuthenticatorId.type)
            else -> sdkWrapper.setPreferredAuthenticator(authenticatorId, promise)
        }
    }

    @ReactMethod
    fun validatePinWithPolicy(pin: String?, promise: Promise) {
        when (pin) {
            null -> promise.rejectWithNullError(Pin.paramName, Pin.type)
            else -> sdkWrapper.validatePinWithPolicy(pin, promise)
        }
    }

    @ReactMethod
    fun registerUser(identityProviderId: String?, scopes: ReadableArray?, promise: Promise) {
        sdkWrapper.registerUser(identityProviderId, scopes, promise)
    }

    @ReactMethod
    fun deregisterUser(profileId: String?, promise: Promise) {
        when (profileId) {
            null -> promise.rejectWithNullError(ProfileId.paramName, ProfileId.type)
            else -> sdkWrapper.deregisterUser(profileId, promise)
        }
    }

    @ReactMethod
    fun deregisterAuthenticator(authenticatorId: String?, promise: Promise) {
        when {
            authenticatorId == null -> promise.rejectWithNullError(AuthenticatorId.paramName, AuthenticatorId.type)
            else -> sdkWrapper.deregisterAuthenticator(authenticatorId, promise)
        }
    }

    @ReactMethod
    fun startSingleSignOn(uri: String?, promise: Promise) {
        uri?.let { sdkWrapper.startSingleSignOn(uri, promise) } ?: promise.rejectWithNullError(Uri.paramName, Uri.type)
    }

    @ReactMethod
    fun cancelBrowserRegistration(promise: Promise) {
        sdkWrapper.cancelBrowserRegistration(promise)
    }

    @ReactMethod
    fun cancelCustomRegistration(message: String?, promise: Promise) {
        when (message) {
            null -> promise.rejectWithNullError(Message.paramName, Message.type)
            else -> sdkWrapper.cancelCustomRegistration(message, promise)
        }
    }

    @ReactMethod
    fun submitCustomRegistrationAction(identityProviderId: String?, token: String?, promise: Promise) {
        when (identityProviderId) {
            null -> promise.rejectWithNullError(IdentityProviderId.paramName, IdentityProviderId.type)
            else -> sdkWrapper.submitCustomRegistrationAction(identityProviderId, token, promise)
        }
    }

    @ReactMethod
    fun getRedirectUri(promise: Promise) {
        sdkWrapper.getRedirectUri(promise)
    }

    @ReactMethod
    fun handleRegistrationCallback(uri: String?, promise: Promise) {
        when (uri) {
            null -> promise.rejectWithNullError(FunctionParams.Uri.paramName, FunctionParams.Uri.type)
            else -> sdkWrapper.handleRegistrationCallback(uri, promise)
        }
    }

    @ReactMethod
    fun cancelPinCreation(promise: Promise) {
        sdkWrapper.cancelPinCreation(promise)
    }

    @ReactMethod
    fun cancelPinAuthentication(promise: Promise) {
        sdkWrapper.cancelPinAuthentication(promise)
    }

    @ReactMethod
    fun changePin(promise: Promise) {
        sdkWrapper.changePin(promise)
    }

    @ReactMethod
    fun submitPin(pinFlow: String?, pin: String?, promise: Promise) {
        when {
            pin == null -> promise.rejectWithNullError(Pin.paramName, Pin.type)
            pinFlow == null -> promise.rejectWithNullError(FunctionParams.PinFlow.paramName, FunctionParams.PinFlow.type)
            else -> sdkWrapper.submitPin(pinFlow, pin, promise)
        }
    }

    @ReactMethod
    fun enrollMobileAuthentication(promise: Promise) {
        sdkWrapper.enrollMobileAuthentication(promise)
    }

    @ReactMethod
    fun acceptMobileAuthConfirmation(promise: Promise) {
        sdkWrapper.acceptMobileAuthConfirmation(promise)
    }

    @ReactMethod
    fun denyMobileAuthConfirmation(promise: Promise) {
        sdkWrapper.denyMobileAuthConfirmation(promise)
    }

    @ReactMethod
    fun handleMobileAuthWithOtp(otpCode: String?, promise: Promise) {
        when (otpCode) {
            null -> promise.rejectWithNullError(OtpCode.paramName, OtpCode.type)
            else -> sdkWrapper.handleMobileAuthWithOtp(otpCode, promise)
        }
    }

    @ReactMethod
    fun getUserProfiles(promise: Promise) {
        sdkWrapper.getUserProfiles(promise)
    }

    @ReactMethod
    fun logout(promise: Promise) {
        sdkWrapper.logout(promise)
    }

    @ReactMethod
    fun authenticateUser(profileId: String?, authenticatorId: String?, promise: Promise) {
        when (profileId) {
            null -> promise.rejectWithNullError(ProfileId.paramName, ProfileId.type)
            else -> sdkWrapper.authenticateUser(profileId, authenticatorId, promise)
        }
    }

    @ReactMethod
    fun authenticateUserImplicitly(profileId: String?, scopes: ReadableArray?, promise: Promise) {
        when (profileId) {
            null -> promise.rejectWithNullError(ProfileId.paramName, ProfileId.type)
            else -> sdkWrapper.authenticateUserImplicitly(profileId, scopes, promise)
        }
    }

    @ReactMethod
    fun authenticateDeviceForResource(scopes: ReadableArray?, promise: Promise) {
        sdkWrapper.authenticateDeviceForResource(scopes, promise)
    }

    @ReactMethod
    fun resourceRequest(type: String?, details: ReadableMap?, promise: Promise) {
        when {
            type == null -> promise.rejectWithNullError(Type.paramName, Type.type)
            details == null -> promise.rejectWithNullError(Details.paramName, Details.type)
            else -> sdkWrapper.resourceRequest(type, details, promise)
        }
    }

    @ReactMethod
    fun getResourceBaseUrl(promise: Promise) {
        sdkWrapper.getResourceBaseUrl(promise)
    }
}
