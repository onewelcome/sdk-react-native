//@todo Later will be transferred to RN Wrapper later
package com.onegini.mobile

import android.net.Uri
import android.util.Log
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import com.onegini.mobile.Constants.CUSTOM_REGISTRATION_NOTIFICATION
import com.onegini.mobile.Constants.CUSTOM_REGISTRATION_NOTIFICATION_FINISH_REGISTRATION
import com.onegini.mobile.Constants.CUSTOM_REGISTRATION_NOTIFICATION_INIT_REGISTRATION
import com.onegini.mobile.Constants.ONEGINI_FINGERPRINT_NOTIFICATION
import com.onegini.mobile.Constants.FINGERPRINT_NOTIFICATION_FINISH_AUTHENTICATION
import com.onegini.mobile.Constants.FINGERPRINT_NOTIFICATION_ON_FINGERPRINT_CAPTURED
import com.onegini.mobile.Constants.FINGERPRINT_NOTIFICATION_ON_NEXT_AUTHENTICATION_ATTEMPT
import com.onegini.mobile.Constants.FINGERPRINT_NOTIFICATION_START_AUTHENTICATION
import com.onegini.mobile.Constants.MOBILE_AUTH_OTP_FINISH_AUTHENTICATION
import com.onegini.mobile.Constants.MOBILE_AUTH_OTP_NOTIFICATION
import com.onegini.mobile.Constants.MOBILE_AUTH_OTP_START_AUTHENTICATION
import com.onegini.mobile.Constants.ONEGINI_PIN_NOTIFICATION
import com.onegini.mobile.Constants.PinFlow
import com.onegini.mobile.OneginiComponets.init
import com.onegini.mobile.clean.wrapper.IOneginiSdkWrapper
import com.onegini.mobile.clean.wrapper.OneginiSdkWrapper
import com.onegini.mobile.exception.OneginReactNativeException
import com.onegini.mobile.exception.OneginReactNativeException.Companion.AUTHENTICATE_DEVICE_ERROR
import com.onegini.mobile.exception.OneginReactNativeException.Companion.FINGERPRINT_IS_NOT_ENABLED
import com.onegini.mobile.exception.OneginReactNativeException.Companion.MOBILE_AUTH_OTP_IS_DISABLED
import com.onegini.mobile.managers.AuthenticatorManager
import com.onegini.mobile.managers.OneginiClientInitializer
import com.onegini.mobile.managers.RegistrationManager
import com.onegini.mobile.mapers.*
import com.onegini.mobile.mapers.CustomInfoMapper.add
import com.onegini.mobile.mapers.UserProfileMapper.add
import com.onegini.mobile.mapers.UserProfileMapper.toUserProfile
import com.onegini.mobile.mapers.UserProfileMapper.toWritableMap
import com.onegini.mobile.model.ApplicationDetails
import com.onegini.mobile.model.ImplicitUserDetails
import com.onegini.mobile.network.AnonymousService
import com.onegini.mobile.network.ImplicitUserService
import com.onegini.mobile.network.UserService
import com.onegini.mobile.sdk.android.handlers.*
import com.onegini.mobile.sdk.android.handlers.error.*
import com.onegini.mobile.sdk.android.model.OneginiAppToWebSingleSignOn
import com.onegini.mobile.sdk.android.model.entity.CustomInfo
import com.onegini.mobile.sdk.android.model.entity.OneginiMobileAuthenticationRequest
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.view.handlers.InitializationHandler
import com.onegini.mobile.view.handlers.customregistration.CustomRegistrationObserver
import com.onegini.mobile.view.handlers.fingerprint.FingerprintAuthenticationObserver
import com.onegini.mobile.view.handlers.mobileauthotp.MobileAuthOtpRequestObserver
import com.onegini.mobile.view.handlers.pins.ChangePinHandler
import com.onegini.mobile.view.handlers.pins.PinNotificationObserver
import io.reactivex.disposables.CompositeDisposable

//
// TODO: codeStyle will by applied in next PR
//

class RNOneginiSdk(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

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
        sdkWrapper = OneginiSdkWrapper(oneginiSDK, reactApplicationContext)

        init(reactContext.applicationContext)
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
    fun startClient(rnConfig: ReadableMap, promise: Promise) {
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
        promise.resolve(toWritableMap(oneginiSDK.oneginiClient.userClient.authenticatedUserProfile))
    }

    @ReactMethod
    fun getAllAuthenticators(profileId: String, promise: Promise) {
        try {
            promise.resolve(OneginiAuthenticatorMapper.toWritableMap(authenticatorManager.getAllAuthenticators(profileId)))
        } catch (e: OneginiError) {
            promise.reject(e.errorType.toString(), e.message)
        }
    }

    @ReactMethod
    fun getRegisteredAuthenticators(profileId: String, promise: Promise) {
        try {
            promise.resolve(OneginiAuthenticatorMapper.toWritableMap(authenticatorManager.getRegisteredAuthenticators(profileId)))
        } catch (e: OneginiError) {
            promise.reject(e.errorType.toString(), e.message)
        }
    }

    @ReactMethod
    fun registerFingerprintAuthenticator(profileId: String, promise: Promise) {
        try {
            authenticatorManager.registerFingerprintAuthenticator(profileId, object : OneginiAuthenticatorRegistrationHandler {
                override fun onSuccess(info: CustomInfo?) {
                    promise.resolve(CustomInfoMapper.toWritableMap(info))
                }

                override fun onError(error: OneginiAuthenticatorRegistrationError?) {
                    promise.reject(error?.errorType.toString(), error?.message)
                }
            })
        } catch (e: OneginiError) {
            promise.reject(e.errorType.toString(), e.message)
        }
    }


    @ReactMethod
    fun isFingerprintAuthenticatorRegistered(profileId: String, promise: Promise) {
        try {
            promise.resolve(authenticatorManager.isFingerprintAuthenticatorRegistered(profileId))
        } catch (e: OneginiError) {
            promise.reject(e.errorType.toString(), e.message)
        }
    }

    @ReactMethod
    fun deregisterFingerprintAuthenticator(profileId: String, promise: Promise) {
        try {
            authenticatorManager.deregisterFingerprintAuthenticator(profileId, object : OneginiAuthenticatorDeregistrationHandler {
                override fun onSuccess() {
                    promise.resolve(null)
                }

                override fun onError(error: OneginiAuthenticatorDeregistrationError?) {
                    promise.reject(error?.errorType.toString(), error?.message)
                }
            })
        } catch (e: OneginiError) {
            promise.reject(e.errorType.toString(), e.message)
        }
    }

    @ReactMethod
    fun submitFingerprintAcceptAuthenticationRequest(promise: Promise) {
        if (oneginiSDK.fingerprintAuthenticationRequestHandler == null) {
            promise.reject(FINGERPRINT_IS_NOT_ENABLED.toString(), " The fingerprint is no enabled. Please check your configuration")
        }
        oneginiSDK.fingerprintAuthenticationRequestHandler!!.acceptAuthenticationRequest()
    }

    @ReactMethod
    fun submitFingerprintDenyAuthenticationRequest(promise: Promise) {
        if (oneginiSDK.fingerprintAuthenticationRequestHandler == null) {
            promise.reject(FINGERPRINT_IS_NOT_ENABLED.toString(), " The fingerprint is no enabled. Please check your configuration")
        }
        oneginiSDK.fingerprintAuthenticationRequestHandler!!.denyAuthenticationRequest()
    }

    @ReactMethod
    fun submitFingerprintFallbackToPin(promise: Promise) {
        if (oneginiSDK.fingerprintAuthenticationRequestHandler == null) {
            promise.reject(FINGERPRINT_IS_NOT_ENABLED.toString(), " The fingerprint is no enabled. Please check your configuration")
        }
        oneginiSDK.fingerprintAuthenticationRequestHandler!!.fallbackToPin()
    }

    @ReactMethod
    fun setPreferredAuthenticator(profileId: String, idOneginiAuthenticator: String, promise: Promise) {
        try {
            authenticatorManager.setPreferredAuthenticator(profileId, idOneginiAuthenticator)
        } catch (e: OneginiError) {
            promise.reject(e.errorType.toString(), e.message)
        }
    }

    @ReactMethod
    fun registerUser(identityProviderId: String?, promise: Promise) {
        registrationManager.registerUser(identityProviderId, object : OneginiRegistrationHandler {
            override fun onSuccess(userProfile: UserProfile?, customInfo: CustomInfo?) {
                promise.resolve(toWritableMap(userProfile))
            }

            override fun onError(error: OneginiRegistrationError) {
                promise.reject(error.errorType.toString(), error.message)
            }
        })
    }

    @ReactMethod
    fun deregisterUser(profileId: String?, promise: Promise) {
        val profile = toUserProfile(profileId!!)
        oneginiSDK.oneginiClient.userClient.deregisterUser(profile, object : OneginiDeregisterUserProfileHandler {
            override fun onSuccess() {
                promise.resolve(null)
            }

            override fun onError(error: OneginiDeregistrationError?) {
                promise.reject(error?.errorType.toString(), error?.message)
            }
        }
        )
    }

    @ReactMethod
    fun startSingleSignOn(url: String, promise: Promise) {
        val targetUri = Uri.parse(url)
        oneginiSDK.oneginiClient.userClient.getAppToWebSingleSignOn(targetUri, object : OneginiAppToWebSingleSignOnHandler {
            override fun onSuccess(oneginiAppToWebSingleSignOn: OneginiAppToWebSingleSignOn) {
                promise.resolve(OneginiAppToWebSingleSignOnMapper.toWritableMap(oneginiAppToWebSingleSignOn))
            }

            override fun onError(error: OneginiAppToWebSingleSignOnError) {
                promise.reject(error.errorType.toString(), error.message)
            }
        })
    }

    @ReactMethod
    fun submitCustomRegistrationAction(customAction: String, identityProviderId: String, token: String?) {
        val action = registrationManager.getSimpleCustomRegistrationAction(identityProviderId)

        if(action == null) {
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
    fun getRedirectUri(promise: Promise) {
        val uri = registrationManager.redirectUri
        val result = Arguments.createMap()
        result.putString("redirectUri", uri)
        promise.resolve(result)
    }

    @ReactMethod
    fun handleRegistrationCallback(uri: String?) {
        registrationManager.handleRegistrationCallback(uri)
    }

    @ReactMethod
    fun cancelRegistration() {
        registrationManager.cancelRegistration()
    }

    @ReactMethod
    fun changePin(promise: Promise) {
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
    fun submitPinAction(flowString: String?, action: String, pin: String?) {
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
    fun authenticateUser(profileId: String?, promise: Promise) {
        val userProfile = UserProfile(profileId)
        oneginiSDK.oneginiClient.userClient.authenticateUser(userProfile, object : OneginiAuthenticationHandler {
            override fun onSuccess(userProfile: UserProfile?, customInfo: CustomInfo?) {
                val result = Arguments.createMap()
                add(result, userProfile)
                add(result, customInfo)
                promise.resolve(result)
            }

            override fun onError(error: OneginiAuthenticationError) {
                promise.reject(error.errorType.toString(), error.message)
            }
        })
    }

    @ReactMethod
    fun enrollMobileAuthentication(promise: Promise) {
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
    fun acceptMobileAuthConfirmation(promise: Promise) {
        val handler = oneginiSDK.mobileAuthOtpRequestHandler
        if (handler == null) {
            promise.reject(MOBILE_AUTH_OTP_IS_DISABLED.toString(), "The Mobile auth Otp is disabled")
        }
        handler!!.acceptAuthenticationRequest()
    }

    @ReactMethod
    fun denyMobileAuthConfirmation(promise: Promise) {
        val handler = oneginiSDK.mobileAuthOtpRequestHandler
        if (handler == null) {
            promise.reject(MOBILE_AUTH_OTP_IS_DISABLED.toString(), "The Mobile auth Otp is disabled")
        }
        handler!!.denyAuthenticationRequest()
    }

    @ReactMethod
    fun handleMobileAuthWithOtp(otpCode: String, promise: Promise) {
        oneginiSDK.oneginiClient.userClient.handleMobileAuthWithOtp(otpCode, object : OneginiMobileAuthWithOtpHandler {
            override fun onSuccess() {
                promise.resolve(null)
            }

            override fun onError(error: OneginiMobileAuthWithOtpError?) {
                promise.reject(error?.errorType.toString(), error?.message)
            }
        })
    }

    @ReactMethod
    fun getUserProfiles(promise: Promise) {
        val profiles = oneginiSDK.oneginiClient.userClient.userProfiles
        promise.resolve(toWritableMap(profiles))
    }

    @ReactMethod
    fun logout(promise: Promise) {
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
    private fun authenticateUserImplicitly(profileId: String?, promise: Promise) {
        Log.d(LOG_TAG, "authenticateUserImplicitly profileId: $profileId");

        val userProfile = authenticatorManager.getUserProfile(profileId)
        if (userProfile == null) {
            promise.reject(OneginReactNativeException.PROFILE_DOES_NOT_EXIST.toString(), "The profileId $profileId does not exist")
        } else {
            oneginiSDK.oneginiClient.userClient
                    .authenticateUserImplicitly(userProfile, arrayOf("read"), object : OneginiImplicitAuthenticationHandler {
                        override fun onSuccess(profile: UserProfile) {
                            promise.resolve(null)
                        }

                        override fun onError(error: OneginiImplicitTokenRequestError) {
                            promise.reject(error.errorType.toString(), error.message)
                        }
                    })
        }
    }

    @ReactMethod
    private fun authenticateDeviceForResource(resourcePath: String, promise: Promise) {
        Log.d(LOG_TAG, "authenticateDeviceForResource resourcePath: $resourcePath");

        oneginiSDK.oneginiClient.deviceClient.authenticateDevice(arrayOf(resourcePath), object : OneginiDeviceAuthenticationHandler {
            override fun onSuccess() {
                promise.resolve(null)
            }

            override fun onError(error: OneginiDeviceAuthenticationError) {
                promise.reject(error.errorType.toString(), error.message)
            }
        })
    }

    // type: User, ImplicitUser, Anonymous
    @ReactMethod
    private fun resourceRequest(type: String, details: ReadableMap, promise: Promise) {
        Log.d(LOG_TAG, "resourceRequest [type: $type] call with $details");

        val requestDetails = ResourceRequestDetailsMapper.toResourceRequestDetails(details)

        when(type) {
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
