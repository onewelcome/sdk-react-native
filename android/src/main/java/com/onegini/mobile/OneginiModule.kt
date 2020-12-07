//@todo Later will be transferred to RN Wrapper later
package com.onegini.mobile

import android.util.Log
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import com.onegini.mobile.Constants.CUSTOM_REGISTRATION_NOTIFICATION
import com.onegini.mobile.Constants.CUSTOM_REGISTRATION_NOTIFICATION_FINISH_REGISTRATION
import com.onegini.mobile.Constants.CUSTOM_REGISTRATION_NOTIFICATION_INIT_REGISTRATION
import com.onegini.mobile.Constants.MOBILE_AUTH_OTP_FINISH_AUTHENTICATION
import com.onegini.mobile.Constants.MOBILE_AUTH_OTP_NOTIFICATION
import com.onegini.mobile.Constants.MOBILE_AUTH_OTP_START_AUTHENTICATION
import com.onegini.mobile.Constants.ONEGINI_PIN_NOTIFICATION
import com.onegini.mobile.Constants.PinFlow
import com.onegini.mobile.OneginiComponets.deregistrationUtil
import com.onegini.mobile.OneginiComponets.init
import com.onegini.mobile.OneginiComponets.userStorage
import com.onegini.mobile.managers.AuthenticatorManager
import com.onegini.mobile.managers.ErrorHelper
import com.onegini.mobile.managers.OneginiClientInitializer
import com.onegini.mobile.managers.RegistrationManager
import com.onegini.mobile.mapers.*
import com.onegini.mobile.mapers.CustomInfoMapper.add
import com.onegini.mobile.mapers.UserProfileMapper.add
import com.onegini.mobile.mapers.UserProfileMapper.toUserProfile
import com.onegini.mobile.mapers.UserProfileMapper.toWritableMap
import com.onegini.mobile.sdk.android.handlers.*
import com.onegini.mobile.sdk.android.handlers.error.*
import com.onegini.mobile.sdk.android.handlers.error.OneginiRegistrationError.RegistrationErrorType
import com.onegini.mobile.sdk.android.model.entity.CustomInfo
import com.onegini.mobile.sdk.android.model.entity.OneginiMobileAuthenticationRequest
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.util.DeregistrationUtil
import com.onegini.mobile.view.handlers.InitializationHandler
import com.onegini.mobile.view.handlers.PinNotificationObserver
import com.onegini.mobile.view.handlers.customregistration.CustomRegistrationObserver
import com.onegini.mobile.view.handlers.mobileauthotp.MobileAuthOtpRequestObserver

class OneginiModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    companion object {
        private const val LOG_TAG = "RNOneginiSdk"
        private const val TAG = "RNOneginiSdk"
    }

    private val reactContext: ReactApplicationContext
    private val registrationManager: RegistrationManager
    private val authenticatorManager: AuthenticatorManager
    private var configModelClassName: String? = null
    private var securityControllerClassName = "com.onegini.mobile.SecurityController"
    private val pinNotificationObserver: PinNotificationObserver
    private val customRegistrationObserver: CustomRegistrationObserver
    private val mobileAuthOtpRequestObserver: MobileAuthOtpRequestObserver
    private val oneginiSDK: OneginiSDK
        private get() = OneginiComponets.oneginiSDK


    init {
        init(reactContext.applicationContext)
        this.reactContext = reactContext
        registrationManager = RegistrationManager(oneginiSDK)
        authenticatorManager = AuthenticatorManager(oneginiSDK)
        pinNotificationObserver = createBridgePinNotificationHandler()
        customRegistrationObserver = createCustomRegistrationObserver()
        mobileAuthOtpRequestObserver = createMobileAuthOtpRequestObserver()
    }

    override fun canOverrideExistingModule(): Boolean {
        return true
    }

    override fun getName(): String {
        return "RNOneginiSdk"
    }

    // React methods will be below
    @ReactMethod
    fun setConfigModelClassName(configModelClassName: String?) {
        this.configModelClassName = configModelClassName
    }

    @ReactMethod
    fun setSecurityControllerClassName(securityControllerClassName: String) {
        this.securityControllerClassName = securityControllerClassName
    }

    @ReactMethod
    fun startClient(rnConfig: ReadableMap, callback: Callback) {
        val config = OneginiReactNativeConfigMapper.toOneginiReactNativeConfig(rnConfig)

        val oneginiClientInitializer = OneginiClientInitializer(
                OneginiComponets.oneginiSDK,
                configModelClassName,
                securityControllerClassName,
                deregistrationUtil,
                userStorage)

        oneginiClientInitializer.startOneginiClient(config, object : InitializationHandler {
            override fun onSuccess() {
                oneginiSDKInitiated()
                val result = Arguments.createMap()
                result.putBoolean("success", true)
                callback.invoke(result)
            }

            override fun onError(errorMessage: String) {
                val result = Arguments.createMap()
                result.putBoolean("success", false)
                result.putString("errorMsg", errorMessage)
                callback.invoke(result)
            }
        })
    }

    private fun oneginiSDKInitiated() {
        oneginiSDK.setPinNotificationObserver(pinNotificationObserver)
        oneginiSDK.setCustomRegistrationObserver(customRegistrationObserver)
        oneginiSDK.setMobileAuthOtpRequestObserver(mobileAuthOtpRequestObserver)
    }

    @ReactMethod
    fun getIdentityProviders(promise: Promise) {
        promise.resolve(OneginiIdentityProviderMapper.toWritableMap(oneginiSDK.oneginiClient.userClient.identityProviders))
    }

    @ReactMethod
    fun getAccessToken(promise: Promise) {
        promise.resolve(oneginiSDK.oneginiClient.accessToken)
    }

    @ReactMethod
    fun getAuthenticatedUserProfile(promise: Promise) {
        promise.resolve(UserProfileMapper.toWritableMap(oneginiSDK.oneginiClient.userClient.authenticatedUserProfile))
    }

    @ReactMethod
    fun registerFingerprintAuthenticator(profileId: String, promise: Promise) {
        try {
            authenticatorManager.registerFingerprintAuthenticator(profileId, object : OneginiAuthenticatorRegistrationHandler {
                override fun onSuccess(info: CustomInfo?) {
                    promise.resolve(CustomInfoMapper.toWritableMap(info))
                }

                override fun onError(error: OneginiAuthenticatorRegistrationError?) {
                    promise.reject(error)
                }
            })
        } catch (e: Exception) {
            promise.reject(e)
        }
    }

    @ReactMethod
    fun isFingerprintAuthenticatorRegistered(profileId: String, promise: Promise) {
        try {
            promise.resolve(authenticatorManager.isFingerprintAuthenticatorRegistered(profileId))
        } catch (e: Exception) {
            promise.reject(e)
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
                    promise.reject(error)
                }
            })
        } catch (e: Exception) {
            promise.reject(e)
        }
    }

    @ReactMethod
    fun registerUser(identityProviderId: String?, callback: Callback) {
        registrationManager.registerUser(identityProviderId, object : OneginiRegistrationHandler {
            override fun onSuccess(userProfile: UserProfile?, customInfo: CustomInfo?) {
                val result = Arguments.createMap()
                result.putBoolean("success", true)
                result.putString("profileId", userProfile?.profileId ?: "")
                try {
                    callback.invoke(result)
                } catch (e: RuntimeException) {
                    Log.w(TAG, "The" + result.toString() + "was not send")
                }
            }

            override fun onError(oneginiRegistrationError: OneginiRegistrationError) {
                @RegistrationErrorType val errorType = oneginiRegistrationError.errorType
                var errorMessage = registrationManager.getErrorMessageByCode(errorType)
                if (errorMessage == null) {
                    errorMessage = oneginiRegistrationError.message
                }
                val result = Arguments.createMap()
                result.putBoolean("success", false)
                result.putString("errorMsg", errorMessage)
                try {
                    callback.invoke(result)
                } catch (e: RuntimeException) {
                    Log.w(TAG, "The" + result.toString() + "was not send")
                }
            }
        })
    }

    @ReactMethod
    fun deregisterUser(profileId: String?, promise: Promise) {
        val profile = toUserProfile(profileId!!)
        DeregistrationUtil(currentActivity!!.applicationContext).onUserDeregistered(profile)
        oneginiSDK.oneginiClient.userClient.deregisterUser(profile, object : OneginiDeregisterUserProfileHandler {
            override fun onSuccess() {
                val result = Arguments.createMap()
                promise.resolve(result)
            }

            override fun onError(oneginiDeregistrationError: OneginiDeregistrationError?) {
                promise.reject(oneginiDeregistrationError)
            }
        }
        )
    }

    @ReactMethod
    fun submitCustomRegistrationReturnSuccess(identityProviderId: String, result: String?) {
        val action = registrationManager.getSimpleCustomRegistrationAction(identityProviderId)
                ?: throw Exception("The $identityProviderId was not configured ")

        action.returnSuccess(result)
    }

    @ReactMethod
    fun submitCustomRegistrationReturnError(identityProviderId: String, errorMessage: String?) {
        val action = registrationManager.getSimpleCustomRegistrationAction(identityProviderId)
                ?: throw Exception("The $identityProviderId was not configured ")

        action.returnError(java.lang.Exception(errorMessage))
    }

    @ReactMethod
    fun getRedirectUri(callback: Callback) {
        val uri = registrationManager.redirectUri
        val result = Arguments.createMap()
        result.putBoolean("success", true)
        result.putString("redirectUri", uri)
        callback.invoke(result)
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

    @ReactMethod
    fun submitCreatePinAction(action: String, pin: String?) {
        when (action) {
            Constants.PIN_ACTION_PROVIDE -> oneginiSDK.createPinRequestHandler.onPinProvided(pin!!.toCharArray(), PinFlow.Create)
            Constants.PIN_ACTION_CANCEL -> oneginiSDK.createPinRequestHandler.pinCancelled(PinFlow.Create)
            else -> Log.e(LOG_TAG, "Got unsupported PIN action: $action")
        }
    }

    @ReactMethod
    fun submitAuthenticationPinAction(action: String, pin: String?) {
        when (action) {
            Constants.PIN_ACTION_PROVIDE -> oneginiSDK.pinAuthenticationRequestHandler.acceptAuthenticationRequest(pin!!.toCharArray())
            Constants.PIN_ACTION_CANCEL -> oneginiSDK.pinAuthenticationRequestHandler.denyAuthenticationRequest()
            else -> Log.e(LOG_TAG, "Got unsupported PIN action: $action")
        }
    }

    @ReactMethod
    fun submitChangePinAction(action: String, pin: String?) {
        when (action) {
            Constants.PIN_ACTION_CHANGE -> oneginiSDK.changePinHandler.onStartChangePin()
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

            override fun onError(oneginiAuthenticationError: OneginiAuthenticationError) {
                promise.reject(oneginiAuthenticationError)
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
                promise.reject(error, OneginiErrorMapper.toWritableMap(error))
            }
        })
    }

    @ReactMethod
    fun submitAcceptMobileAuthOtp(promise: Promise) {
        val handler = oneginiSDK.mobileAuthOtpRequestHandler
        if (handler == null) {
            promise.reject(Exception("The Mobile auth Otp is disabled"))
        }
        handler!!.acceptAuthenticationRequest()
    }

    @ReactMethod
    fun submitDenyMobileAuthOtp(promise: Promise) {
        val handler = oneginiSDK.mobileAuthOtpRequestHandler
        if (handler == null) {
            promise.reject(Exception("The Mobile auth Otp is disabled"))
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
                promise.reject(error)
            }
        })
    }

    @ReactMethod
    fun getUserProfiles(promise: Promise) {
        val profiles = oneginiSDK.oneginiClient.userClient.userProfiles
        promise.resolve(toWritableMap(profiles))
    }

    private fun createMobileAuthOtpRequestObserver(): MobileAuthOtpRequestObserver {
        return object : MobileAuthOtpRequestObserver {
            override fun startAuthentication(request: OneginiMobileAuthenticationRequest?) {
                val map = Arguments.createMap()
                map.putString("action", MOBILE_AUTH_OTP_START_AUTHENTICATION)
                OneginiMobileAuthenticationRequestMapper.add(map, request)
                reactApplicationContext.getJSModule(RCTDeviceEventEmitter::class.java).emit(MOBILE_AUTH_OTP_NOTIFICATION, map)
            }

            override fun finishAuthentication() {
                val map = Arguments.createMap()
                map.putString("action", MOBILE_AUTH_OTP_FINISH_AUTHENTICATION)
                reactApplicationContext.getJSModule(RCTDeviceEventEmitter::class.java).emit(MOBILE_AUTH_OTP_NOTIFICATION, map)
            }

        }
    }

    private fun createCustomRegistrationObserver(): CustomRegistrationObserver {
        return object : CustomRegistrationObserver {
            override fun initRegistration(idProvider: String, info: CustomInfo?) {
                val map = Arguments.createMap()
                map.putString("action", CUSTOM_REGISTRATION_NOTIFICATION_INIT_REGISTRATION)
                OneginiIdentityProviderMapper.add(map, idProvider)
                add(map, info)
                reactApplicationContext.getJSModule(RCTDeviceEventEmitter::class.java).emit(CUSTOM_REGISTRATION_NOTIFICATION, map)
            }

            override fun finishRegistration(idProvider: String, info: CustomInfo?) {
                val map = Arguments.createMap()
                map.putString("action", CUSTOM_REGISTRATION_NOTIFICATION_FINISH_REGISTRATION)
                OneginiIdentityProviderMapper.add(map, idProvider)
                add(map, info)
                reactApplicationContext.getJSModule(RCTDeviceEventEmitter::class.java).emit(CUSTOM_REGISTRATION_NOTIFICATION, map)
            }
        }
    }

    private fun createBridgePinNotificationHandler(): PinNotificationObserver {
        return object : PinNotificationObserver {
            override fun onNotify(event: String, flow: PinFlow) {
                val data = Arguments.createMap()
                when (event) {
                    Constants.PIN_NOTIFICATION_OPEN_VIEW -> {
                        data.putString("action", Constants.PIN_NOTIFICATION_OPEN_VIEW)
                        data.putString("flow", flow.flowString)
                    }
                    Constants.PIN_NOTIFICATION_CONFIRM_VIEW -> {
                        data.putString("action", Constants.PIN_NOTIFICATION_CONFIRM_VIEW)
                        data.putString("flow", flow.flowString)
                    }
                    Constants.PIN_NOTIFICATION_CLOSE_VIEW -> {
                        data.putString("action", Constants.PIN_NOTIFICATION_CLOSE_VIEW)
                        data.putString("flow", flow.flowString)
                    }
                    Constants.PIN_NOTIFICATION_CHANGED -> {
                        data.putString("action", Constants.PIN_NOTIFICATION_CHANGED)
                        data.putString("flow", flow.flowString)
                    }
                    else -> Log.e(LOG_TAG, "Got unsupported PIN notification type: $event")
                }
                if (data.getString("action") != null) {
                    reactApplicationContext.getJSModule(RCTDeviceEventEmitter::class.java).emit(ONEGINI_PIN_NOTIFICATION, data)
                }
            }

            override fun onError(message: String) {
                val data = Arguments.createMap()
                data.putString("action", Constants.PIN_NOTIFICATION_SHOW_ERROR)
                data.putString("errorMsg", message)
                reactApplicationContext.getJSModule(RCTDeviceEventEmitter::class.java).emit(ONEGINI_PIN_NOTIFICATION, data)
            }
        }
    }

    @ReactMethod
    fun logout(callback: Callback) {
        val userClient = oneginiSDK.oneginiClient.userClient
        val userProfile = userClient.authenticatedUserProfile
        userClient.logout(
                object : OneginiLogoutHandler {
                    override fun onSuccess() {
                        val result = Arguments.createMap()
                        result.putBoolean("success", true)
                        callback.invoke(result)
                    }

                    override fun onError(oneginiLogoutError: OneginiLogoutError?) {
                        val message = ErrorHelper.handleLogoutError(oneginiLogoutError, userProfile, reactContext)
                        val result = Arguments.createMap()
                        result.putBoolean("success", false)
                        result.putString("errorMsg", message)
                        callback.invoke(result)
                    }
                }
        )
    }
}