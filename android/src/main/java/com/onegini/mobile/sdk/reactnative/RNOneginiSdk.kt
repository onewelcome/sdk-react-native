// @todo Later will be transferred to RN Wrapper later
package com.onegini.mobile.sdk.reactnative

import android.net.Uri
import android.util.Log
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.onegini.mobile.sdk.android.handlers.OneginiAppToWebSingleSignOnHandler
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticationHandler
import com.onegini.mobile.sdk.android.handlers.OneginiDeregisterUserProfileHandler
import com.onegini.mobile.sdk.android.handlers.OneginiDeviceAuthenticationHandler
import com.onegini.mobile.sdk.android.handlers.OneginiImplicitAuthenticationHandler
import com.onegini.mobile.sdk.android.handlers.OneginiLogoutHandler
import com.onegini.mobile.sdk.android.handlers.OneginiMobileAuthEnrollmentHandler
import com.onegini.mobile.sdk.android.handlers.OneginiMobileAuthWithOtpHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiAppToWebSingleSignOnError
import com.onegini.mobile.sdk.android.handlers.error.OneginiAuthenticationError
import com.onegini.mobile.sdk.android.handlers.error.OneginiChangePinError
import com.onegini.mobile.sdk.android.handlers.error.OneginiDeregistrationError
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
import com.onegini.mobile.sdk.reactnative.OneginiComponets.init
import com.onegini.mobile.sdk.reactnative.clean.wrapper.IOneginiSdkWrapper
import com.onegini.mobile.sdk.reactnative.clean.wrapper.OneginiSdkWrapper
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.reactnative.handlers.pins.ChangePinHandler
import com.onegini.mobile.sdk.reactnative.managers.AuthenticatorManager
import com.onegini.mobile.sdk.reactnative.managers.AuthenticatorManager.DeregistrationCallback
import com.onegini.mobile.sdk.reactnative.managers.AuthenticatorManager.RegistrationCallback
import com.onegini.mobile.sdk.reactnative.managers.RegistrationManager
import com.onegini.mobile.sdk.reactnative.mapers.CustomInfoMapper
import com.onegini.mobile.sdk.reactnative.mapers.CustomInfoMapper.add
import com.onegini.mobile.sdk.reactnative.mapers.JsonMapper
import com.onegini.mobile.sdk.reactnative.mapers.OneginiAppToWebSingleSignOnMapper
import com.onegini.mobile.sdk.reactnative.mapers.OneginiAuthenticatorMapper
import com.onegini.mobile.sdk.reactnative.mapers.ResourceRequestDetailsMapper
import com.onegini.mobile.sdk.reactnative.mapers.ScopesMapper
import com.onegini.mobile.sdk.reactnative.mapers.UserProfileMapper.add
import com.onegini.mobile.sdk.reactnative.mapers.UserProfileMapper.toUserProfile
import com.onegini.mobile.sdk.reactnative.mapers.UserProfileMapper.toWritableMap
import com.onegini.mobile.sdk.reactnative.network.AnonymousService
import com.onegini.mobile.sdk.reactnative.network.ImplicitUserService
import com.onegini.mobile.sdk.reactnative.network.UserService
import io.reactivex.rxjava3.disposables.CompositeDisposable

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
        get() = OneginiComponets.oneginiSDK

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
        sdkWrapper.getAuthenticatedUserProfile(promise)
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
    fun isFingerprintAuthenticatorRegistered(profileId: String, promise: Promise) {
        try {
            promise.resolve(authenticatorManager.isFingerprintAuthenticatorRegistered(profileId))
        } catch (e: OneginiError) {
            promise.reject(e.errorType.toString(), e.message)
        }
    }

    @ReactMethod
    fun deregisterFingerprintAuthenticator(profileId: String, promise: Promise) {
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
    fun setPreferredAuthenticator(profileId: String, idOneginiAuthenticator: String, promise: Promise) {
        try {
            authenticatorManager.setPreferredAuthenticator(profileId, idOneginiAuthenticator)
        } catch (e: OneginiError) {
            promise.reject(e.errorType.toString(), e.message)
        }
    }

    @ReactMethod
    fun registerUser(identityProviderId: String?, scopes: ReadableArray, promise: Promise) {
        sdkWrapper.registerUser(identityProviderId, scopes, promise)
    }

    @ReactMethod
    fun deregisterUser(profileId: String?, promise: Promise) {
        val profile = toUserProfile(profileId!!)
        oneginiSDK.oneginiClient.userClient.deregisterUser(
            profile,
            object : OneginiDeregisterUserProfileHandler {
                override fun onSuccess() {
                    promise.resolve(null)
                }

                override fun onError(error: OneginiDeregistrationError) {
                    promise.reject(error.errorType.toString(), error.message)
                }
            }
        )
    }

    @ReactMethod
    fun startSingleSignOn(url: String, promise: Promise) {
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
    fun submitCustomRegistrationAction(customAction: String, identityProviderId: String, token: String?) {
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
        oneginiSDK.oneginiClient.userClient.authenticateUser(
            userProfile,
            object : OneginiAuthenticationHandler {
                override fun onSuccess(userProfile: UserProfile, customInfo: CustomInfo?) {
                    val result = Arguments.createMap()
                    add(result, userProfile)
                    add(result, customInfo)
                    promise.resolve(result)
                }

                override fun onError(error: OneginiAuthenticationError) {
                    promise.reject(error.errorType.toString(), error.message)
                }
            }
        )
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
        handler!!.acceptAuthenticationRequest()
    }

    @ReactMethod
    fun denyMobileAuthConfirmation(promise: Promise) {
        val handler = oneginiSDK.mobileAuthOtpRequestHandler
        if (handler == null) {
            promise.reject(OneginiWrapperErrors.MOBILE_AUTH_OTP_IS_DISABLED.code, OneginiWrapperErrors.MOBILE_AUTH_OTP_IS_DISABLED.message)
            return
        }
        handler!!.denyAuthenticationRequest()
    }

    @ReactMethod
    fun handleMobileAuthWithOtp(otpCode: String, promise: Promise) {
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

                override fun onError(error: OneginiLogoutError) {
                    promise.reject(error.errorType.toString(), error.message)
                }
            }
        )
    }

    @ReactMethod
    private fun authenticateUserImplicitly(profileId: String, scopes: ReadableArray, promise: Promise) {
        val scopesArray = ScopesMapper.toStringArray(scopes)
        Log.d(LOG_TAG, "authenticateUserImplicitly profileId: $profileId")

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
    private fun authenticateDeviceForResource(scopes: ReadableArray, promise: Promise) {
        val scopesArray = ScopesMapper.toStringArray(scopes)
        Log.d(LOG_TAG, "authenticateDeviceForResource scopes: $scopes")

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
    private fun resourceRequest(type: String, details: ReadableMap, promise: Promise) {
        Log.d(LOG_TAG, "resourceRequest [type: $type] call with $details")

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
