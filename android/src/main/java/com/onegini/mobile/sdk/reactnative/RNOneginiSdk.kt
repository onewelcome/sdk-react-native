package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.onegini.mobile.sdk.android.handlers.OneginiChangePinHandler
import com.onegini.mobile.sdk.android.handlers.OneginiDeviceAuthenticationHandler
import com.onegini.mobile.sdk.android.handlers.OneginiImplicitAuthenticationHandler
import com.onegini.mobile.sdk.android.handlers.OneginiLogoutHandler
import com.onegini.mobile.sdk.android.handlers.OneginiMobileAuthEnrollmentHandler
import com.onegini.mobile.sdk.android.handlers.OneginiMobileAuthWithOtpHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiChangePinError
import com.onegini.mobile.sdk.android.handlers.error.OneginiDeviceAuthenticationError
import com.onegini.mobile.sdk.android.handlers.error.OneginiError
import com.onegini.mobile.sdk.android.handlers.error.OneginiImplicitTokenRequestError
import com.onegini.mobile.sdk.android.handlers.error.OneginiLogoutError
import com.onegini.mobile.sdk.android.handlers.error.OneginiMobileAuthEnrollmentError
import com.onegini.mobile.sdk.android.handlers.error.OneginiMobileAuthWithOtpError
import com.onegini.mobile.sdk.android.model.OneginiAuthenticator
import com.onegini.mobile.sdk.android.model.entity.CustomInfo
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.Constants.PinFlow
import com.onegini.mobile.sdk.reactnative.RNOneginiSdk.FunctionParams.*
import com.onegini.mobile.sdk.reactnative.clean.wrapper.OneginiSdkWrapper
import com.onegini.mobile.sdk.reactnative.module.RNOneginiSdkModule
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.reactnative.exception.PARAM_CAN_NOT_BE_NULL
import com.onegini.mobile.sdk.reactnative.handlers.fingerprint.FingerprintAuthenticationRequestHandler
import com.onegini.mobile.sdk.reactnative.handlers.mobileauthotp.MobileAuthOtpRequestHandler
import com.onegini.mobile.sdk.reactnative.handlers.pins.CreatePinRequestHandler
import com.onegini.mobile.sdk.reactnative.handlers.pins.PinAuthenticationRequestHandler
import com.onegini.mobile.sdk.reactnative.handlers.registration.RegistrationRequestHandler
import com.onegini.mobile.sdk.reactnative.exception.CANCEL_CUSTOM_REGISTRATION_NOT_ALLOWED
import com.onegini.mobile.sdk.reactnative.handlers.customregistration.SimpleCustomRegistrationAction
import com.onegini.mobile.sdk.reactnative.managers.AuthenticatorManager
import com.onegini.mobile.sdk.reactnative.managers.AuthenticatorManager.DeregistrationCallback
import com.onegini.mobile.sdk.reactnative.managers.AuthenticatorManager.RegistrationCallback
import com.onegini.mobile.sdk.reactnative.managers.RegistrationManager
import com.onegini.mobile.sdk.reactnative.mapers.CustomInfoMapper
import com.onegini.mobile.sdk.reactnative.mapers.JsonMapper
import com.onegini.mobile.sdk.reactnative.mapers.ResourceRequestDetailsMapper
import com.onegini.mobile.sdk.reactnative.mapers.ScopesMapper
import com.onegini.mobile.sdk.reactnative.network.AnonymousService
import com.onegini.mobile.sdk.reactnative.network.ImplicitUserService
import com.onegini.mobile.sdk.reactnative.network.UserService
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.lang.Exception
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
    lateinit var userService: UserService
    @Inject
    lateinit var implicitUserService: ImplicitUserService
    @Inject
    lateinit var anonymousService: AnonymousService
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

    private val disposables = CompositeDisposable()


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
        IdOneginiAuthenticator("idOneginiAuthenticator", "string"),
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
    fun isFingerprintAuthenticatorRegistered(profileId: String?, promise: Promise) {
        when (profileId) {
            null -> promise.rejectWithNullError(ProfileId.paramName, ProfileId.type)
            else -> {
                authenticatorManager.getUserProfile(profileId)?.let { userProfile ->
                    val authenticator = authenticatorManager.getRegisteredAuthenticators(userProfile, OneginiAuthenticator.FINGERPRINT)
                    promise.resolve(authenticator != null)
                } ?: promise.reject(OneginiWrapperErrors.PROFILE_DOES_NOT_EXIST.code.toString(), OneginiWrapperErrors.PROFILE_DOES_NOT_EXIST.message)
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
    fun setPreferredAuthenticator(profileId: String?, idOneginiAuthenticator: String?, promise: Promise) {
        when {
            profileId == null -> promise.rejectWithNullError(ProfileId.paramName, ProfileId.type)
            idOneginiAuthenticator == null -> promise.rejectWithNullError(IdOneginiAuthenticator.paramName, IdOneginiAuthenticator.type)
            else -> sdkWrapper.setPreferredAuthenticator(profileId, idOneginiAuthenticator, promise)
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
    fun startSingleSignOn(uri: String?, promise: Promise) {
        uri?.let { sdkWrapper.startSingleSignOn(uri, promise) } ?: promise.rejectWithNullError(Uri.paramName, Uri.type)
    }

    @ReactMethod
    fun cancelBrowserRegistration(promise: Promise) {
        try {
            registrationRequestHandler.cancelRegistration()
            return promise.resolve(null)
        } catch (exception: OneginiReactNativeException) {
            promise.reject(exception.errorType.toString(), exception.message)
        }
    }

    @ReactMethod
    fun cancelCustomRegistration(message: String?, promise: Promise) {
        when (message) {
            null -> promise.rejectWithNullError(Message.paramName, Message.type)
            else -> {
                getActiveCustomRegistrationAction()?.let { action ->
                    try {
                        action.returnError(Exception(message))
                        return promise.resolve(null)
                    } catch (exception: OneginiReactNativeException) {
                        promise.reject(OneginiWrapperErrors.ACTION_NOT_ALLOWED.code.toString(), CANCEL_CUSTOM_REGISTRATION_NOT_ALLOWED)
                    }
                } ?: promise.reject(OneginiWrapperErrors.ACTION_NOT_ALLOWED.code.toString(), CANCEL_CUSTOM_REGISTRATION_NOT_ALLOWED)
            }
        }
    }

    private fun getActiveCustomRegistrationAction(): SimpleCustomRegistrationAction? {
        for (action in oneginiSDK.simpleCustomRegistrationActions) {
            if (action.isInProgress()){
                return action
            }
        }
        return null
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
        try {
            createPinRequestHandler.cancelPin()
        } catch (exception: OneginiReactNativeException) {
            promise.reject(OneginiWrapperErrors.PIN_CREATION_NOT_IN_PROGRESS.code.toString(), OneginiWrapperErrors.PIN_CREATION_NOT_IN_PROGRESS.message)
        }
    }

    @ReactMethod
    fun cancelPinAuthentication(promise: Promise) {
        return try {
            pinAuthenticationRequestHandler.denyAuthenticationRequest()
            promise.resolve(null)
        } catch (exception: OneginiReactNativeException) {
            promise.reject(exception.errorType.toString(), exception.message)
        }
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
        val scopesArray = ScopesMapper.toStringArray(scopes)
        oneginiSDK.oneginiClient.deviceClient.authenticateDevice(
            scopesArray,
            object : OneginiDeviceAuthenticationHandler {
                override fun onSuccess() {
                    promise.resolve(null)
                }

                override fun onError(error: OneginiDeviceAuthenticationError) {
                    promise.reject(error.errorType.toString(), error.message)
                }
            }
        )
    }

    @ReactMethod
    fun resourceRequest(type: String?, details: ReadableMap?, promise: Promise) {
        when {
            type == null -> promise.rejectWithNullError(Type.paramName, Type.type)
            details == null -> promise.rejectWithNullError(Details.paramName, Details.type)
            else -> {
                val requestDetails = ResourceRequestDetailsMapper.toResourceRequestDetails(details)
                when (type) {
                    "User" -> {
                        disposables.add(
                            userService
                                .getResource(requestDetails)
                                .subscribe({
                                    promise.resolve(JsonMapper.toWritableMap(it))
                                }) { throwable -> promise.reject(OneginiWrapperErrors.RESOURCE_CALL_ERROR.code.toString(), throwable) }
                        )
                    }
                    "ImplicitUser" -> {
                        disposables.add(
                            implicitUserService
                                .getResource(requestDetails)
                                .subscribe({
                                    promise.resolve(JsonMapper.toWritableMap(it))
                                }) { throwable -> promise.reject(OneginiWrapperErrors.RESOURCE_CALL_ERROR.code.toString(), throwable) }
                        )
                    }
                    "Anonymous" -> {
                        disposables.add(
                            anonymousService
                                .getResource(requestDetails)
                                .subscribe({
                                    promise.resolve(JsonMapper.toWritableMap(it))
                                }) { throwable -> promise.reject(OneginiWrapperErrors.RESOURCE_CALL_ERROR.code.toString(), throwable) }
                        )
                    }
                }
            }
        }
    }

    override fun onCatalystInstanceDestroy() {
        disposables.clear()
        super.onCatalystInstanceDestroy()
    }
}
