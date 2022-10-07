// @todo Later will be transferred to RN Wrapper later
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
import com.onegini.mobile.sdk.android.model.entity.CustomInfo
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.Constants.PinFlow
import com.onegini.mobile.sdk.reactnative.clean.wrapper.OneginiSdkWrapper
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
        rnConfig ?: promise.rejectWithNullError("rnConfig", "ReadableMap").run { return }
        sdkWrapper.startClient(rnConfig, promise)
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
        profileId ?: promise.rejectWithNullError("profileId", "String").run { return }
        sdkWrapper.getAllAuthenticators(profileId, promise)
    }

    @ReactMethod
    fun getRegisteredAuthenticators(profileId: String?, promise: Promise) {
        profileId ?: promise.rejectWithNullError("profileId", "String").run { return }
        sdkWrapper.getRegisteredAuthenticators(profileId, promise)
    }

    @ReactMethod
    fun registerFingerprintAuthenticator(profileId: String?, promise: Promise) {
        profileId ?: promise.rejectWithNullError("profileId", "String").run { return }
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

    @ReactMethod
    fun isFingerprintAuthenticatorRegistered(profileId: String?, promise: Promise) {
        profileId ?: promise.rejectWithNullError("profileId", "String").run { return }
        try {
            promise.resolve(authenticatorManager.isFingerprintAuthenticatorRegistered(profileId))
        } catch (e: OneginiError) {
            promise.reject(e.errorType.toString(), e.message)
        }
    }

    @ReactMethod
    fun deregisterFingerprintAuthenticator(profileId: String?, promise: Promise) {
        profileId ?: promise.rejectWithNullError("profileId", "String").run { return }
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

    @ReactMethod
    fun submitFingerprintAcceptAuthenticationRequest(promise: Promise) {
        oneginiSDK.fingerprintAuthenticationRequestHandler?.acceptAuthenticationRequest()
    }

    @ReactMethod
    fun submitFingerprintDenyAuthenticationRequest(promise: Promise) {
        oneginiSDK.fingerprintAuthenticationRequestHandler?.denyAuthenticationRequest()
    }

    @ReactMethod
    fun submitFingerprintFallbackToPin(promise: Promise) {
        oneginiSDK.fingerprintAuthenticationRequestHandler?.fallbackToPin()
    }

    @ReactMethod
    fun setPreferredAuthenticator(profileId: String?, idOneginiAuthenticator: String?, promise: Promise) {
        profileId ?: promise.rejectWithNullError("profileId", "String").run { return }
        idOneginiAuthenticator ?: promise.rejectWithNullError("idOneginiAuthenticator", "String").run { return }
        try {
            authenticatorManager.setPreferredAuthenticator(profileId, idOneginiAuthenticator)
        } catch (e: OneginiError) {
            promise.reject(e.errorType.toString(), e.message)
        }
    }

    @ReactMethod
    fun validatePinWithPolicy(pin: String?, promise: Promise) {
        pin ?: promise.rejectWithNullError("pin", "String").run { return }
        sdkWrapper.validatePinWithPolicy(pin, promise)
    }

    @ReactMethod
    fun registerUser(identityProviderId: String?, scopes: ReadableArray?, promise: Promise) {
        sdkWrapper.registerUser(identityProviderId, scopes, promise)
    }

    @ReactMethod
    fun deregisterUser(profileId: String?, promise: Promise) {
        profileId ?: promise.rejectWithNullError("profileId", "String").run { return }
        sdkWrapper.deregisterUser(profileId, promise)
    }

    @ReactMethod
    fun startSingleSignOn(url: String?, promise: Promise) {
        url ?: promise.rejectWithNullError("url", "String").run { return }
        val targetUri = Uri.parse(url)
        oneginiSDK.oneginiClient.userClient.getAppToWebSingleSignOn(
            targetUri,
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

    @ReactMethod
    fun submitCustomRegistrationAction(customAction: String?, identityProviderId: String?, token: String?, promise: Promise) {
        identityProviderId ?: promise.rejectWithNullError("identityProviderId", "String").run { return }
        token ?: promise.rejectWithNullError("token", "String").run { return }

        val action = registrationManager.getSimpleCustomRegistrationAction(identityProviderId)

        if (action == null) {
            return promise.reject(OneginiWrapperErrors.IDENTITY_PROVIDER_NOT_FOUND.code, OneginiWrapperErrors.IDENTITY_PROVIDER_NOT_FOUND.message)
        }

        when (customAction) {
            Constants.CUSTOM_REGISTRATION_ACTION_PROVIDE -> action.returnSuccess(token)
            Constants.CUSTOM_REGISTRATION_ACTION_CANCEL -> action.returnError(java.lang.Exception(token))
            else -> {
                promise.reject(OneginiWrapperErrors.PARAMETERS_NOT_CORRECT.code, OneginiWrapperErrors.PARAMETERS_NOT_CORRECT.message + ". Incorrect customAction supplied: $customAction")
            }
        }
        promise.resolve(null)
    }

    @ReactMethod
    fun getRedirectUri(promise: Promise) {
        sdkWrapper.getRedirectUri(promise)
    }

    @ReactMethod
    fun handleRegistrationCallback(uri: String?, promise: Promise) {
        uri ?: promise.rejectWithNullError("uri", "String").run { return }
        return if (registrationManager.handleRegistrationCallback(uri)) {
            promise.resolve(null)
        } else {
            promise.reject(OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.code, OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS.message)
        }
    }

    @ReactMethod
    fun cancelRegistration(promise: Promise) {
        registrationManager.cancelRegistration()
        promise.resolve(null)
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
                pin ?: promise.rejectWithNullError("pin", "String").run { return }
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
                oneginiSDK.pinAuthenticationRequestHandler.acceptAuthenticationRequest(pin.toCharArray())
                return promise.resolve(null)
            }
            PinFlow.Create.toString() -> {
                oneginiSDK.createPinRequestHandler.onPinProvided(pin.toCharArray())
                return promise.resolve(null)
            }
            PinFlow.Change.toString() -> {
                oneginiSDK.createPinRequestHandler.onPinProvided(pin.toCharArray())
                return promise.resolve(null)
            } else -> {
                promise.reject(OneginiWrapperErrors.PARAMETERS_NOT_CORRECT.code, OneginiWrapperErrors.PARAMETERS_NOT_CORRECT.message + ". Incorrect Pinflow supplied: $pinFlow")
            }
        }
    }

    private fun handleSubmitPinActionCancel(pinFlow: String?, promise: Promise) {
        when (pinFlow) {
            PinFlow.Authentication.toString() -> {
                oneginiSDK.pinAuthenticationRequestHandler.denyAuthenticationRequest()
                return promise.resolve(null)
            }
            PinFlow.Create.toString() -> {
                oneginiSDK.createPinRequestHandler.pinCancelled()
                return promise.resolve(null)
            }
            PinFlow.Change.toString() -> {
                oneginiSDK.createPinRequestHandler.pinCancelled()
                return promise.resolve(null)
            } else -> {
            promise.reject(OneginiWrapperErrors.PARAMETERS_NOT_CORRECT.code, OneginiWrapperErrors.PARAMETERS_NOT_CORRECT.message + ". Incorrect Pinflow supplied: $pinFlow")
            }
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
        handler.acceptAuthenticationRequest()
    }

    @ReactMethod
    fun denyMobileAuthConfirmation(promise: Promise) {
        val handler = oneginiSDK.mobileAuthOtpRequestHandler
        if (handler == null) {
            promise.reject(OneginiWrapperErrors.MOBILE_AUTH_OTP_IS_DISABLED.code, OneginiWrapperErrors.MOBILE_AUTH_OTP_IS_DISABLED.message)
            return
        }
        handler.denyAuthenticationRequest()
    }

    @ReactMethod
    fun handleMobileAuthWithOtp(otpCode: String?, promise: Promise) {
        otpCode ?: promise.rejectWithNullError("otpCode", "String").run { return }
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
        profileId ?: promise.rejectWithNullError("profileId", "String").run { return }
        sdkWrapper.authenticateUser(profileId, authenticatorId, promise)
    }

    @ReactMethod
    fun authenticateUserImplicitly(profileId: String?, scopes: ReadableArray?, promise: Promise) {
        profileId ?: promise.rejectWithNullError("profileId", "String").run { return }
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

    // type: User, ImplicitUser, Anonymous
    @ReactMethod
    fun resourceRequest(type: String?, details: ReadableMap?, promise: Promise) {
        type ?: promise.rejectWithNullError("type", "String").run { return }
        details ?: promise.rejectWithNullError("details", "String").run { return }
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

    //

    override fun onCatalystInstanceDestroy() {
        disposables.clear()
        super.onCatalystInstanceDestroy()
    }
}
