package com.onegini.mobile

import android.net.Uri
import android.util.Log
import com.facebook.react.bridge.*
import com.onegini.mobile.Constants.PinFlow
import com.onegini.mobile.OneginiComponets.init
import com.onegini.mobile.clean.wrapper.IOneginiSdkWrapper
import com.onegini.mobile.clean.wrapper.OneginiSdkWrapper
import com.onegini.mobile.exception.OneginReactNativeException
import com.onegini.mobile.exception.OneginReactNativeException.Companion.AUTHENTICATE_DEVICE_ERROR
import com.onegini.mobile.exception.OneginReactNativeException.Companion.FINGERPRINT_IS_NOT_ENABLED
import com.onegini.mobile.exception.OneginReactNativeException.Companion.MOBILE_AUTH_OTP_IS_DISABLED
import com.onegini.mobile.managers.AuthenticatorManager
import com.onegini.mobile.managers.RegistrationManager
import com.onegini.mobile.mapers.*
import com.onegini.mobile.mapers.CustomInfoMapper.add
import com.onegini.mobile.mapers.UserProfileMapper.add
import com.onegini.mobile.mapers.UserProfileMapper.toWritableMap
import com.onegini.mobile.network.AnonymousService
import com.onegini.mobile.network.ImplicitUserService
import com.onegini.mobile.network.UserService
import com.onegini.mobile.sdk.android.handlers.*
import com.onegini.mobile.sdk.android.handlers.error.*
import com.onegini.mobile.sdk.android.model.OneginiAppToWebSingleSignOn
import com.onegini.mobile.sdk.android.model.entity.CustomInfo
import com.onegini.mobile.view.handlers.pins.ChangePinHandler
import io.reactivex.disposables.CompositeDisposable

class RNOneginiSdk(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext), IOneginiSdkWrapper {

    companion object {
        private const val LOG_TAG = "RNOneginiSdk"
        private const val TAG = "RNOneginiSdk"
    }

    private val sdkWrapper: IOneginiSdkWrapper

    private val reactContext: ReactApplicationContext
    private val registrationManager: RegistrationManager
    private val authenticatorManager: AuthenticatorManager

    private val oneginiSDK: OneginiSDK
        private get() = OneginiComponets.oneginiSDK

    private val disposables = CompositeDisposable()

    init {
        init(reactContext.applicationContext)

        sdkWrapper = OneginiSdkWrapper(oneginiSDK, reactApplicationContext)

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
    override fun registerFingerprintAuthenticator(profileId: String, promise: Promise) {
        try {
            authenticatorManager.registerFingerprintAuthenticator(
                profileId,
                object : OneginiAuthenticatorRegistrationHandler {
                    override fun onSuccess(info: CustomInfo?) {
                        promise.resolve(CustomInfoMapper.toWritableMap(info))
                    }

                    override fun onError(error: OneginiAuthenticatorRegistrationError?) {
                        promise.reject(error?.errorType.toString(), error?.message)
                    }
                }
            )
        } catch (e: OneginiError) {
            promise.reject(e.errorType.toString(), e.message)
        }
    }

    @ReactMethod
    override fun isFingerprintAuthenticatorRegistered(profileId: String, promise: Promise) {
        try {
            promise.resolve(authenticatorManager.isFingerprintAuthenticatorRegistered(profileId))
        } catch (e: OneginiError) {
            promise.reject(e.errorType.toString(), e.message)
        }
    }

    @ReactMethod
    override fun deregisterFingerprintAuthenticator(profileId: String, promise: Promise) {
        try {
            authenticatorManager.deregisterFingerprintAuthenticator(
                profileId,
                object : OneginiAuthenticatorDeregistrationHandler {
                    override fun onSuccess() {
                        promise.resolve(null)
                    }

                    override fun onError(error: OneginiAuthenticatorDeregistrationError?) {
                        promise.reject(error?.errorType.toString(), error?.message)
                    }
                }
            )
        } catch (e: OneginiError) {
            promise.reject(e.errorType.toString(), e.message)
        }
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
        try {
            authenticatorManager.setPreferredAuthenticator(profileId, idOneginiAuthenticator)
        } catch (e: OneginiError) {
            promise.reject(e.errorType.toString(), e.message)
        }
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
        oneginiSDK.oneginiClient.userClient.enrollUserForMobileAuth(object : OneginiMobileAuthEnrollmentHandler {
            override fun onSuccess() {
                promise.resolve(null)
            }

            override fun onError(error: OneginiMobileAuthEnrollmentError?) {
                promise.reject(error?.errorType.toString(), error?.message)
            }
        })
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
        oneginiSDK.oneginiClient.userClient.handleMobileAuthWithOtp(
            otpCode,
            object : OneginiMobileAuthWithOtpHandler {
                override fun onSuccess() {
                    promise.resolve(null)
                }

                override fun onError(error: OneginiMobileAuthWithOtpError?) {
                    promise.reject(error?.errorType.toString(), error?.message)
                }
            }
        )
    }

    @ReactMethod
    override fun getUserProfiles(promise: Promise) {
        sdkWrapper.getUserProfiles(promise)
    }

    @ReactMethod
    override fun logout(promise: Promise) {
        val userClient = oneginiSDK.oneginiClient.userClient
        userClient.logout(
            object : OneginiLogoutHandler {
                override fun onSuccess() {
                    promise.resolve(null)
                }

                override fun onError(error: OneginiLogoutError?) {
                    promise.reject(error?.errorType.toString(), error?.message)
                }
            }
        )
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
        Log.d(LOG_TAG, "authenticateDeviceForResource resourcePath: $resourcePath")

        oneginiSDK.oneginiClient.deviceClient.authenticateDevice(
            arrayOf(resourcePath),
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
    override fun resourceRequest(type: String, details: ReadableMap, promise: Promise) {
        Log.d(LOG_TAG, "resourceRequest [type: $type] call with $details")

        val requestDetails = ResourceRequestDetailsMapper.toResourceRequestDetails(details)

        when (type) {
            "User" -> {
                disposables.add(
                    UserService.getInstance()
                        .getResource(requestDetails)
                        .subscribe({
                            promise.resolve(JsonMapper.toWritableMap(it))
                        }) { throwable -> promise.reject(OneginReactNativeException.IMPLICIT_USER_DETAILS_ERROR.toString(), throwable.message) }
                )
            }
            "ImplicitUser" -> {
                disposables.add(
                    ImplicitUserService.getInstance()
                        .getResource(requestDetails)
                        .subscribe({
                            promise.resolve(JsonMapper.toWritableMap(it))
                        }) { throwable -> promise.reject(OneginReactNativeException.IMPLICIT_USER_DETAILS_ERROR.toString(), throwable.message) }
                )
            }
            "Anonymous" -> {
                disposables.add(
                    AnonymousService.getInstance()
                        .getResource(requestDetails)
                        .subscribe({
                            promise.resolve(JsonMapper.toWritableMap(it))
                        }) { throwable -> promise.reject(AUTHENTICATE_DEVICE_ERROR.toString(), throwable.message) }
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
