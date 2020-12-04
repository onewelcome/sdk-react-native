//@todo Later will be transferred to RN Wrapper later
package com.onegini.mobile

import android.util.Log
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter
import com.onegini.mobile.Constants.CUSTOM_REGISTRATION_NOTIFICATION
import com.onegini.mobile.Constants.CUSTOM_REGISTRATION_NOTIFICATION_FINISH_REGISTRATION
import com.onegini.mobile.Constants.CUSTOM_REGISTRATION_NOTIFICATION_INIT_REGISTRATION
import com.onegini.mobile.Constants.ONEGINI_PIN_NOTIFICATION
import com.onegini.mobile.Constants.PinFlow
import com.onegini.mobile.OneginiComponets.deregistrationUtil
import com.onegini.mobile.OneginiComponets.init
import com.onegini.mobile.OneginiComponets.userStorage
import com.onegini.mobile.helpers.ErrorHelper
import com.onegini.mobile.helpers.OneginiClientInitializer
import com.onegini.mobile.helpers.RegistrationHelper
import com.onegini.mobile.mapers.CustomInfoMapper.add
import com.onegini.mobile.mapers.OneginiErrorMapper
import com.onegini.mobile.mapers.OneginiIdentityProviderMapper
import com.onegini.mobile.mapers.OneginiReactNativeConfigMapper
import com.onegini.mobile.mapers.UserProfileMapper.add
import com.onegini.mobile.mapers.UserProfileMapper.toUserProfile
import com.onegini.mobile.mapers.UserProfileMapper.toWritableMap
import com.onegini.mobile.sdk.android.handlers.*
import com.onegini.mobile.sdk.android.handlers.error.*
import com.onegini.mobile.sdk.android.handlers.error.OneginiRegistrationError.RegistrationErrorType
import com.onegini.mobile.sdk.android.model.entity.CustomInfo
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.util.DeregistrationUtil
import com.onegini.mobile.view.handlers.InitializationHandler
import com.onegini.mobile.view.handlers.PinNotificationObserver
import com.onegini.mobile.view.handlers.customregistration.CustomRegistrationObserver

class RNOneginiSdk(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    companion object {
        private const val LOG_TAG = "RNOneginiSdk"
        private const val TAG = "RNOneginiSdk"
    }

    private val reactContext: ReactApplicationContext
    private val registrationHelper: RegistrationHelper
    private var configModelClassName: String? = null
    private var securityControllerClassName = "com.onegini.mobile.SecurityController"
    private val pinNotificationObserver: PinNotificationObserver
    private val customRegistrationObserver: CustomRegistrationObserver
    private val oneginiSDK: OneginiSDK
        private get() = OneginiComponets.oneginiSDK


    init {
        init(reactContext.applicationContext)
        this.reactContext = reactContext
        registrationHelper = RegistrationHelper(oneginiSDK)
        pinNotificationObserver = createBridgePinNotificationHandler()
        customRegistrationObserver = createCustomRegistrationObserver()
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
    fun registerUser(identityProviderId: String?, callback: Callback) {
        registrationHelper.registerUser(identityProviderId, object : OneginiRegistrationHandler {
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
                var errorMessage = registrationHelper.getErrorMessageByCode(errorType)
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
        val action = registrationHelper.getSimpleCustomRegistrationAction(identityProviderId)
                ?: throw Exception("The $identityProviderId was not configured ")

        action.returnSuccess(result)
    }

    @ReactMethod
    fun submitCustomRegistrationReturnError(identityProviderId: String, errorMessage: String?) {
        val action = registrationHelper.getSimpleCustomRegistrationAction(identityProviderId)
                ?: throw Exception("The $identityProviderId was not configured ")

        action.returnError(java.lang.Exception(errorMessage))
    }

    @ReactMethod
    fun getRedirectUri(callback: Callback) {
        val uri = registrationHelper.redirectUri
        val result = Arguments.createMap()
        result.putBoolean("success", true)
        result.putString("redirectUri", uri)
        callback.invoke(result)
    }

    @ReactMethod
    fun handleRegistrationCallback(uri: String?) {
        registrationHelper.handleRegistrationCallback(uri)
    }

    @ReactMethod
    fun cancelRegistration() {
        registrationHelper.cancelRegistration()
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
    fun getUserProfiles(promise: Promise) {
        val profiles = oneginiSDK.oneginiClient.userClient.userProfiles
        promise.resolve(toWritableMap(profiles))
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
