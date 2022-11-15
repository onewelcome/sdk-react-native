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
import com.onegini.mobile.sdk.reactnative.exception.CANCEL_CUSTOM_REGISTRATION_NOT_ALLOWED
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.reactnative.exception.PARAM_CAN_NOT_BE_NULL
import com.onegini.mobile.sdk.reactnative.managers.AuthenticatorManager
import com.onegini.mobile.sdk.reactnative.managers.AuthenticatorManager.DeregistrationCallback
import com.onegini.mobile.sdk.reactnative.managers.AuthenticatorManager.RegistrationCallback
import com.onegini.mobile.sdk.reactnative.managers.RegistrationManager
import com.onegini.mobile.sdk.reactnative.mapers.CustomInfoMapper
import com.onegini.mobile.sdk.reactnative.mapers.JsonMapper
import com.onegini.mobile.sdk.reactnative.mapers.OneginiAppToWebSingleSignOnMapper
import com.onegini.mobile.sdk.reactnative.mapers.ResourceRequestDetailsMapper
import com.onegini.mobile.sdk.reactnative.mapers.ScopesMapper
import com.onegini.mobile.sdk.reactnative.network.AnonymousService
import com.onegini.mobile.sdk.reactnative.network.ImplicitUserService
import com.onegini.mobile.sdk.reactnative.network.UserService
import io.reactivex.rxjava3.disposables.CompositeDisposable


class RNOneginiSdk(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    private val sdkWrapper: OneginiSdkWrapper

    private val reactContext: ReactApplicationContext
    private val registrationManager: RegistrationManager
    private val authenticatorManager: AuthenticatorManager

    private val oneginiSDK: OneginiSDK
        get() = OneginiComponents.oneginiSDK

    private val disposables = CompositeDisposable()

    init {
        OneginiComponents.init(reactContext)

        sdkWrapper = OneginiSdkWrapper(oneginiSDK)

        this.reactContext = reactContext
        registrationManager = RegistrationManager(oneginiSDK)
        authenticatorManager = AuthenticatorManager(oneginiSDK)
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
        Message("message", "string"),
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
        oneginiSDK.fingerprintAuthenticationRequestHandler?.let { fingerprintHandler ->
            fingerprintHandler.acceptAuthenticationRequest()
            promise.resolve(null)
        } ?:
        promise.reject(OneginiWrapperErrors.FINGERPRINT_IS_NOT_ENABLED.code, OneginiWrapperErrors.FINGERPRINT_IS_NOT_ENABLED.message)
    }

    @ReactMethod
    fun submitFingerprintDenyAuthenticationRequest(promise: Promise) {
        oneginiSDK.fingerprintAuthenticationRequestHandler?.let { fingerprintHandler ->
            fingerprintHandler.denyAuthenticationRequest()
            promise.resolve(null)
        } ?:
        promise.reject(OneginiWrapperErrors.FINGERPRINT_IS_NOT_ENABLED.code, OneginiWrapperErrors.FINGERPRINT_IS_NOT_ENABLED.message)
    }

    @ReactMethod
    fun submitFingerprintFallbackToPin(promise: Promise) {
        oneginiSDK.fingerprintAuthenticationRequestHandler?.let { fingerprintHandler ->
            fingerprintHandler.fallbackToPin()
            promise.resolve(null)
        } ?:
        promise.reject(OneginiWrapperErrors.FINGERPRINT_IS_NOT_ENABLED.code, OneginiWrapperErrors.FINGERPRINT_IS_NOT_ENABLED.message)
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
    fun cancelBrowserRegistration(promise: Promise) {
        try {
            oneginiSDK.registrationRequestHandler.cancelRegistration()
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
                for (action in oneginiSDK.simpleCustomRegistrationActions) {
                    try {
                        action.returnError(Exception(message))
                        return promise.resolve(null)
                    } catch (exception: OneginiReactNativeException) {}
                }
                promise.reject(OneginiWrapperErrors.ACTION_NOT_ALLOWED.code, CANCEL_CUSTOM_REGISTRATION_NOT_ALLOWED)
            }
        }
    }

    @ReactMethod
    fun submitCustomRegistrationAction(identityProviderId: String?, token: String?, promise: Promise) {
        when (identityProviderId) {
            null -> promise.rejectWithNullError(IdentityProviderId.paramName, IdentityProviderId.type)
            else -> {
                val action = registrationManager.getSimpleCustomRegistrationAction(identityProviderId)
                    ?: return promise.reject(OneginiWrapperErrors.IDENTITY_PROVIDER_NOT_FOUND.code, OneginiWrapperErrors.IDENTITY_PROVIDER_NOT_FOUND.message)
                try {
                    action.returnSuccess(token)
                    promise.resolve(null)
                } catch (exception: OneginiReactNativeException) {
                    promise.reject(OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.code, OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.message)
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
    fun cancelPinCreation(promise: Promise) {
        try {
            oneginiSDK.createPinRequestHandler.cancelPin()
        } catch (exception: OneginiReactNativeException) {
            promise.reject(OneginiWrapperErrors.PIN_CREATION_NOT_IN_PROGRESS.code, OneginiWrapperErrors.PIN_CREATION_NOT_IN_PROGRESS.message)
        }
    }

    @ReactMethod
    fun cancelPinAuthentication(promise: Promise) {
        return try {
            oneginiSDK.pinAuthenticationRequestHandler.denyAuthenticationRequest()
            promise.resolve(null)
        } catch (exception: OneginiReactNativeException) {
            promise.reject(exception.errorType.toString(), exception.message)
        }
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
    fun submitPin(pinFlow: String?, pin: String?, promise: Promise) {
        when (pin) {
            null -> promise.rejectWithNullError(Pin.paramName, Pin.type)
            else -> when (pinFlow) {
                PinFlow.Authentication.toString() -> {
                    return handleSubmitAuthPin(pin, promise)
                }
                PinFlow.Create.toString() -> {
                    return handleSubmitCreatePin(pin, promise)
                }
            }
        }
    }

    private fun handleSubmitCreatePin(pin: String, promise: Promise) {
        return try {
            oneginiSDK.createPinRequestHandler.onPinProvided(pin.toCharArray())
            promise.resolve(null)
        } catch (exception: OneginiReactNativeException) {
            promise.reject(
                OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.code,
                OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.message
            )
        }
    }

    private fun handleSubmitAuthPin(pin: String, promise: Promise) {
        return try {
            oneginiSDK.pinAuthenticationRequestHandler.acceptAuthenticationRequest(pin.toCharArray())
            promise.resolve(null)
        } catch (exception: OneginiReactNativeException) {
            promise.reject(
                OneginiWrapperErrors.AUTHENTICATION_NOT_IN_PROGRESS.code,
                OneginiWrapperErrors.AUTHENTICATION_NOT_IN_PROGRESS.message
            )
        }
    }

    @ReactMethod
    fun enrollMobileAuthentication(promise: Promise) {
        oneginiSDK.oneginiClient.userClient.enrollUserForMobileAuth(object : OneginiMobileAuthEnrollmentHandler {
            override fun onSuccess() {
                promise.resolve(null)
            }

            override fun onError(error: OneginiMobileAuthEnrollmentError) {
                promise.reject(error.errorType.toString(), error.message)
            }
        })
    }

    @ReactMethod
    fun acceptMobileAuthConfirmation(promise: Promise) {
        val handler = oneginiSDK.mobileAuthOtpRequestHandler
        if (handler == null) {
            promise.reject(OneginiWrapperErrors.MOBILE_AUTH_OTP_IS_DISABLED.code, OneginiWrapperErrors.MOBILE_AUTH_OTP_IS_DISABLED.message)
            return
        }
        when (handler.acceptAuthenticationRequest()) {
            true -> promise.resolve(null)
            false -> promise.reject(OneginiWrapperErrors.MOBILE_AUTH_OTP_NOT_IN_PROGRESS.code, OneginiWrapperErrors.MOBILE_AUTH_OTP_NOT_IN_PROGRESS.message)
        }
    }

    @ReactMethod
    fun denyMobileAuthConfirmation(promise: Promise) {
        val handler = oneginiSDK.mobileAuthOtpRequestHandler
        if (handler == null) {
            promise.reject(OneginiWrapperErrors.MOBILE_AUTH_OTP_IS_DISABLED.code, OneginiWrapperErrors.MOBILE_AUTH_OTP_IS_DISABLED.message)
            return
        }
        when (handler.denyAuthenticationRequest()) {
            true -> promise.resolve(null)
            false -> promise.reject(OneginiWrapperErrors.MOBILE_AUTH_OTP_NOT_IN_PROGRESS.code, OneginiWrapperErrors.MOBILE_AUTH_OTP_NOT_IN_PROGRESS.message)
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
                            UserService.getInstance()
                                .getResource(requestDetails)
                                .subscribe({
                                    promise.resolve(JsonMapper.toWritableMap(it))
                                }) { throwable -> promise.reject(OneginiWrapperErrors.RESOURCE_CALL_ERROR.code, throwable) }
                        )
                    }
                    "ImplicitUser" -> {
                        disposables.add(
                            ImplicitUserService.getInstance()
                                .getResource(requestDetails)
                                .subscribe({
                                    promise.resolve(JsonMapper.toWritableMap(it))
                                }) { throwable -> promise.reject(OneginiWrapperErrors.RESOURCE_CALL_ERROR.code, throwable) }
                        )
                    }
                    "Anonymous" -> {
                        disposables.add(
                            AnonymousService.getInstance()
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
