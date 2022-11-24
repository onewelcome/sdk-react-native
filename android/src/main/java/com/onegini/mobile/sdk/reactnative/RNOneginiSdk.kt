package com.onegini.mobile.sdk.reactnative

import android.net.Uri
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.onegini.mobile.sdk.android.handlers.OneginiAppToWebSingleSignOnHandler
import com.onegini.mobile.sdk.android.handlers.OneginiChangePinHandler
import com.onegini.mobile.sdk.android.handlers.OneginiDeviceAuthenticationHandler
import com.onegini.mobile.sdk.android.handlers.OneginiImplicitAuthenticationHandler
import com.onegini.mobile.sdk.android.handlers.OneginiLogoutHandler
import com.onegini.mobile.sdk.android.handlers.OneginiMobileAuthEnrollmentHandler
import com.onegini.mobile.sdk.android.handlers.OneginiMobileAuthWithOtpHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiAppToWebSingleSignOnError
import com.onegini.mobile.sdk.android.handlers.error.OneginiChangePinError
import com.onegini.mobile.sdk.android.handlers.error.OneginiDeviceAuthenticationError
import com.onegini.mobile.sdk.android.handlers.error.OneginiError
import com.onegini.mobile.sdk.android.handlers.error.OneginiImplicitTokenRequestError
import com.onegini.mobile.sdk.android.handlers.error.OneginiLogoutError
import com.onegini.mobile.sdk.android.handlers.error.OneginiMobileAuthEnrollmentError
import com.onegini.mobile.sdk.android.handlers.error.OneginiMobileAuthWithOtpError
import com.onegini.mobile.sdk.android.model.OneginiAppToWebSingleSignOn
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
import com.onegini.mobile.sdk.reactnative.managers.AuthenticatorManager
import com.onegini.mobile.sdk.reactnative.managers.AuthenticatorManager.DeregistrationCallback
import com.onegini.mobile.sdk.reactnative.managers.AuthenticatorManager.RegistrationCallback
import com.onegini.mobile.sdk.reactnative.managers.RegistrationManager
import com.onegini.mobile.sdk.reactnative.mapers.CustomInfoMapper
import com.onegini.mobile.sdk.reactnative.mapers.JsonMapper
import com.onegini.mobile.sdk.reactnative.mapers.OneginiAppToWebSingleSignOnMapper
import com.onegini.mobile.sdk.reactnative.mapers.ResourceRequestDetailsMapper
import com.onegini.mobile.sdk.reactnative.mapers.ScopesMapper
import com.onegini.mobile.sdk.reactnative.module.SecureResourceClientModule
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
        Uri("uri", "string"),
        IdentityProviderId("identityProviderId", "string"),
        OtpCode("otpCode", "string"),
        Type("type", "string"),
        Details("details", "string"),
    }

    override fun canOverrideExistingModule(): Boolean {
        return true
    }

    override fun getName(): String {
        return "RNOneginiSdk"
    }

    private fun Promise.rejectWithNullError(paramName: String, paramType: String){
        this.reject(OneginiWrapperErrors.PARAMETERS_NOT_CORRECT.code, String.format(PARAM_CAN_NOT_BE_NULL, paramName, paramType))
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
                } ?: promise.reject(OneginiWrapperErrors.PROFILE_DOES_NOT_EXIST.code, OneginiWrapperErrors.PROFILE_DOES_NOT_EXIST.message)
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
            false -> promise.reject(OneginiWrapperErrors.FINGERPRINT_IS_NOT_ENABLED.code, OneginiWrapperErrors.FINGERPRINT_IS_NOT_ENABLED.message)
            true -> {
                fingerprintAuthenticationRequestHandler.acceptAuthenticationRequest()
                promise.resolve(null)
            }
        }
    }

    @ReactMethod
    fun submitFingerprintDenyAuthenticationRequest(promise: Promise) {
        when (oneginiSDK.config.enableFingerprint) {
            false -> promise.reject(OneginiWrapperErrors.FINGERPRINT_IS_NOT_ENABLED.code, OneginiWrapperErrors.FINGERPRINT_IS_NOT_ENABLED.message)
            true -> {
                fingerprintAuthenticationRequestHandler.denyAuthenticationRequest()
                promise.resolve(null)
            }
        }
    }

    @ReactMethod
    fun submitFingerprintFallbackToPin(promise: Promise) {
        when (oneginiSDK.config.enableFingerprint) {
            false -> promise.reject(OneginiWrapperErrors.FINGERPRINT_IS_NOT_ENABLED.code, OneginiWrapperErrors.FINGERPRINT_IS_NOT_ENABLED.message)
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
            else -> {
                try {
                    authenticatorManager.setPreferredAuthenticator(profileId, idOneginiAuthenticator)
                    promise.resolve(null)
                } catch (e: OneginiError) {
                    promise.reject(e.errorType.toString(), e.message)
                }
            }
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
        when (uri) {
            null -> promise.rejectWithNullError(FunctionParams.Uri.paramName, FunctionParams.Uri.type)
            else -> {
                oneginiSDK.oneginiClient.userClient.getAppToWebSingleSignOn(
                    Uri.parse(uri),
                    object : OneginiAppToWebSingleSignOnHandler {
                        override fun onSuccess(oneginiAppToWebSingleSignOn: OneginiAppToWebSingleSignOn) {
                            promise.resolve(OneginiAppToWebSingleSignOnMapper.toWritableMap(oneginiAppToWebSingleSignOn))
                        }

                        override fun onError(error: OneginiAppToWebSingleSignOnError) {
                            promise.reject(error.errorType.toString(), error.message)
                        }
                    }
                )
            }
        }

    }

    @ReactMethod
    fun submitCustomRegistrationAction(customAction: String?, identityProviderId: String?, token: String?, promise: Promise) {
        when (identityProviderId) {
            null -> promise.rejectWithNullError(IdentityProviderId.paramName, IdentityProviderId.type)
            else -> {
                val action = registrationManager.getSimpleCustomRegistrationAction(identityProviderId)

                if (action == null) {
                    return promise.reject(OneginiWrapperErrors.IDENTITY_PROVIDER_NOT_FOUND.code, OneginiWrapperErrors.IDENTITY_PROVIDER_NOT_FOUND.message)
                }

                when (customAction) {
                    Constants.CUSTOM_REGISTRATION_ACTION_PROVIDE -> {
                        try {
                            action.returnSuccess(token)
                            promise.resolve(null)
                        } catch (exception: OneginiReactNativeException) {
                            promise.reject(OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.code, OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.message)
                        }
                    }
                    Constants.CUSTOM_REGISTRATION_ACTION_CANCEL -> {
                        try {
                            action.returnError(Exception(token))
                            promise.resolve(null)
                        } catch (exception: OneginiReactNativeException) {
                            promise.reject(OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.code, OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.message)
                        }
                    }
                    else -> {
                        promise.reject(OneginiWrapperErrors.PARAMETERS_NOT_CORRECT.code, OneginiWrapperErrors.PARAMETERS_NOT_CORRECT.message + ". Incorrect customAction supplied: $customAction")
                    }
                }
            }
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
            else -> {
                return if (registrationManager.handleRegistrationCallback(uri)) {
                    promise.resolve(null)
                } else {
                    promise.reject(OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.code, OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.message)
                }
            }
        }
    }

    @ReactMethod
    fun cancelRegistration(promise: Promise) {
        for (action in oneginiSDK.simpleCustomRegistrationActions) {
            try {
                cancelCreatePinSilent()
                action.returnError(null)
                return promise.resolve(null)
            } catch (exception: OneginiReactNativeException) {}
        }

        try {
            registrationRequestHandler.cancelRegistration()
            cancelCreatePinSilent()
            return promise.resolve(null)
        } catch (exception: OneginiReactNativeException) {}

        promise.reject(
            OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.code,
            OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.message
        )
    }

    private fun cancelCreatePinSilent() {
        try {
            createPinRequestHandler.cancelPin()
        } catch (exception: OneginiReactNativeException) {}
    }

    @ReactMethod
    fun changePin(promise: Promise) {
        oneginiSDK.oneginiClient.userClient.changePin(object : OneginiChangePinHandler {
            override fun onSuccess() {
                promise.resolve(null)
            }

            override fun onError(error: OneginiChangePinError) {
                promise.reject(error?.errorType.toString(), error?.message)
            }
        })
    }

    @ReactMethod
    fun submitPinAction(pinFlow: String?, action: String?, pin: String?, promise: Promise) {
        when (action) {
            Constants.PIN_ACTION_PROVIDE -> {
                // TODO: Fix this nullGuarding pattern when apply the changes in RNP-126
                pin ?: promise.rejectWithNullError(Pin.paramName, Pin.type).run { return }
                handleSubmitPinActionProvide(pinFlow, pin, promise)
                return
            }
            Constants.PIN_ACTION_CANCEL -> {
                handleSubmitPinActionCancel(pinFlow, promise)
                return
            }
            else -> {
                promise.reject(OneginiWrapperErrors.PARAMETERS_NOT_CORRECT.code, OneginiWrapperErrors.PARAMETERS_NOT_CORRECT.message + ". Incorrect action supplied: $action")
                return
            }
        }
    }

    private fun handleSubmitPinActionProvide(pinFlow: String?, pin: String, promise: Promise) {
        when (pinFlow) {
            PinFlow.Authentication.toString() -> {
                return try {
                    pinAuthenticationRequestHandler.acceptAuthenticationRequest(pin.toCharArray())
                    promise.resolve(null)
                } catch (exception: OneginiReactNativeException) {
                    promise.reject(OneginiWrapperErrors.AUTHENTICATION_NOT_IN_PROGRESS.code, OneginiWrapperErrors.AUTHENTICATION_NOT_IN_PROGRESS.message)
                }
            }
            PinFlow.Create.toString() -> {
                return try {
                    createPinRequestHandler.onPinProvided(pin.toCharArray())
                    promise.resolve(null)
                } catch (exception: OneginiReactNativeException) {
                    promise.reject(OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.code, OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.message)
                }
            }
        }
    }

    private fun handleSubmitPinActionCancel(pinFlow: String?, promise: Promise) {
        when (pinFlow) {
            PinFlow.Authentication.toString() -> {
                pinAuthenticationRequestHandler.denyAuthenticationRequest()
                return promise.resolve(null)
            }
            PinFlow.Create.toString() -> {
                return try {
                    createPinRequestHandler.cancelPin()
                    promise.resolve(null)
                } catch (exception: OneginiReactNativeException) {
                    promise.reject(OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.code, OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.message)
                }
            }
        }
    }

    @ReactMethod
    fun enrollMobileAuthentication(promise: Promise) {
        sdkWrapper.enrollMobileAuthentication(promise)
    }

    @ReactMethod
    fun acceptMobileAuthConfirmation(promise: Promise) {
        when (oneginiSDK.config.enableMobileAuthenticationOtp) {
            false -> promise.reject(OneginiWrapperErrors.MOBILE_AUTH_OTP_IS_DISABLED.code, OneginiWrapperErrors.MOBILE_AUTH_OTP_IS_DISABLED.message)
            true -> {
                when (mobileAuthOtpRequestHandler.acceptAuthenticationRequest()) {
                    true -> promise.resolve(null)
                    false -> promise.reject(OneginiWrapperErrors.MOBILE_AUTH_OTP_NOT_IN_PROGRESS.code, OneginiWrapperErrors.MOBILE_AUTH_OTP_NOT_IN_PROGRESS.message)
                }
            }
        }
    }

    @ReactMethod
    fun denyMobileAuthConfirmation(promise: Promise) {
        when (oneginiSDK.config.enableMobileAuthenticationOtp) {
            false -> promise.reject(OneginiWrapperErrors.MOBILE_AUTH_OTP_IS_DISABLED.code, OneginiWrapperErrors.MOBILE_AUTH_OTP_IS_DISABLED.message)
            true -> {
                when (mobileAuthOtpRequestHandler.denyAuthenticationRequest()) {
                    true -> promise.resolve(null)
                    false -> promise.reject(OneginiWrapperErrors.MOBILE_AUTH_OTP_NOT_IN_PROGRESS.code, OneginiWrapperErrors.MOBILE_AUTH_OTP_NOT_IN_PROGRESS.message)
                }
            }
        }
    }

    @ReactMethod
    fun handleMobileAuthWithOtp(otpCode: String?, promise: Promise) {
        when (otpCode) {
            null -> promise.rejectWithNullError(OtpCode.paramName, OtpCode.type)
            else -> {
                oneginiSDK.oneginiClient.userClient.handleMobileAuthWithOtp(
                    otpCode,
                    object : OneginiMobileAuthWithOtpHandler {
                        override fun onSuccess() {
                            promise.resolve(null)
                        }

                        override fun onError(error: OneginiMobileAuthWithOtpError) {
                            promise.reject(error.errorType.toString(), error.message)
                        }
                    }
                )
            }
        }
    }

    @ReactMethod
    fun getUserProfiles(promise: Promise) {
        sdkWrapper.getUserProfiles(promise)
    }

    @ReactMethod
    fun logout(promise: Promise) {
        val userClient = oneginiSDK.oneginiClient.userClient
        userClient.logout(
            object : OneginiLogoutHandler {
                override fun onSuccess() {
                    promise.resolve(null)
                }

                override fun onError(error: OneginiLogoutError) {
                    promise.reject(error.errorType.toString(), error.message)
                }
            }
        )
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
            else -> {
                val scopesArray = ScopesMapper.toStringArray(scopes)
                val userProfile = authenticatorManager.getUserProfile(profileId)
                if (userProfile == null) {
                    promise.reject(OneginiWrapperErrors.PROFILE_DOES_NOT_EXIST.code, OneginiWrapperErrors.PROFILE_DOES_NOT_EXIST.message)
                } else {
                    oneginiSDK.oneginiClient.userClient
                        .authenticateUserImplicitly(
                            userProfile, scopesArray,
                            object : OneginiImplicitAuthenticationHandler {
                                override fun onSuccess(profile: UserProfile) {
                                    promise.resolve(null)
                                }
                                override fun onError(error: OneginiImplicitTokenRequestError) {
                                    promise.reject(error.errorType.toString(), error.message)
                                }
                            }
                        )
                }
            }
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
                                }) { throwable -> promise.reject(OneginiWrapperErrors.RESOURCE_CALL_ERROR.code, throwable) }
                        )
                    }
                    "ImplicitUser" -> {
                        disposables.add(
                            implicitUserService
                                .getResource(requestDetails)
                                .subscribe({
                                    promise.resolve(JsonMapper.toWritableMap(it))
                                }) { throwable -> promise.reject(OneginiWrapperErrors.RESOURCE_CALL_ERROR.code, throwable) }
                        )
                    }
                    "Anonymous" -> {
                        disposables.add(
                            anonymousService
                                .getResource(requestDetails)
                                .subscribe({
                                    promise.resolve(JsonMapper.toWritableMap(it))
                                }) { throwable -> promise.reject(OneginiWrapperErrors.RESOURCE_CALL_ERROR.code, throwable) }
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
