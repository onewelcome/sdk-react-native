package com.onegini.mobile.sdk.reactnative

import android.content.Context
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.onegini.mobile.sdk.android.client.OneginiClient
import com.onegini.mobile.sdk.android.client.UserClient
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticatorDeregistrationHandler
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticatorRegistrationHandler
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiFingerprintCallback
import com.onegini.mobile.sdk.android.model.OneginiAuthenticator
import com.onegini.mobile.sdk.android.model.entity.CustomInfo
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test


class RNOneginiSdkFingerprintTest {

    companion object {
        val USER_PROFILE = UserProfile("t12345")
    }

    private lateinit var rNOneginiSdk: RNOneginiSdk

    @MockK(relaxed = true)
    lateinit var reactApplicationContext: ReactApplicationContext

    @MockK
    lateinit var appContext: Context

    @MockK
    lateinit var userClient: UserClient

    @MockK
    lateinit var rCTDeviceEventEmitter: DeviceEventManagerModule.RCTDeviceEventEmitter

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        every { reactApplicationContext.applicationContext } returns appContext
        every { appContext.applicationContext } returns appContext
        every { reactApplicationContext.getJSModule<DeviceEventManagerModule.RCTDeviceEventEmitter>(any()) } returns rCTDeviceEventEmitter
        rNOneginiSdk = RNOneginiSdk(reactApplicationContext)
    }

    @Test
    fun registerFingerprintAuthenticator_PROFILE_DOES_NOT_EXIST() {
        val oneginiClient = setupOneginiClient()
        every { userClient.userProfiles } returns emptySet()
        Utils.startClient(rNOneginiSdk, oneginiClient, null, true)

        val promise = mockkClass(Promise::class, relaxed = true)
        rNOneginiSdk.registerFingerprintAuthenticator("test", promise)
        every { promise.reject(OneginiWrapperErrors.PROFILE_DOES_NOT_EXIST.toString(), any<String>()) }
    }

    @Test
    fun registerFingerprintAuthenticator_AUTHENTICATOR_DOES_NOT_EXIST() {
        val oneginiClient = setupOneginiClient()

        val profiles = HashSet<UserProfile>()
        profiles.add(USER_PROFILE)
        every { userClient.userProfiles } returns profiles
        every { userClient.getNotRegisteredAuthenticators(USER_PROFILE) } returns emptySet()
        Utils.startClient(rNOneginiSdk, oneginiClient, null, true)

        val promise = mockkClass(Promise::class, relaxed = true)
        rNOneginiSdk.registerFingerprintAuthenticator(USER_PROFILE.profileId, promise)
        every { promise.reject(OneginiWrapperErrors.AUTHENTICATOR_DOES_NOT_EXIST.toString(), any<String>()) }
    }

    @Test
    fun registerFingerprintAuthenticator_onSuccess() {
        val oneginiClient = setupOneginiClient()

        val profiles = HashSet<UserProfile>()
        profiles.add(USER_PROFILE)
        every { userClient.userProfiles } returns profiles

        val authenticators = HashSet<OneginiAuthenticator>()
        val oneginiAuthenticator = mockkClass(OneginiAuthenticator::class, relaxed = true)
        every { oneginiAuthenticator.type } returns OneginiAuthenticator.FINGERPRINT
        authenticators.add(oneginiAuthenticator)
        every { userClient.getNotRegisteredAuthenticators(USER_PROFILE) } returns authenticators
        Utils.startClient(rNOneginiSdk, oneginiClient, null, true)

        val handlerSlot = slot<OneginiAuthenticatorRegistrationHandler>()
        every {
            userClient.registerAuthenticator(oneginiAuthenticator, capture(handlerSlot))
        } answers {}

        val promise = mockkClass(Promise::class, relaxed = true)
        rNOneginiSdk.registerFingerprintAuthenticator(USER_PROFILE.profileId, promise)

        val returnMap = mockkClass(WritableMap::class, relaxed = true)

        mockkStatic(Arguments::class)
        every { Arguments.createMap() } returns returnMap

        val customInfo = CustomInfo(10, "data")
        handlerSlot.captured.onSuccess(customInfo)

        verify { returnMap.putString("data", customInfo.data) }
        verify { returnMap.putInt("status", customInfo.status) }

        verify { promise.resolve(returnMap) }
    }

    fun deregisterFingerprintAuthenticator_onSuccess() {
        val oneginiClient = setupOneginiClient()

        val profiles = HashSet<UserProfile>()
        profiles.add(USER_PROFILE)
        every { userClient.userProfiles } returns profiles

        val authenticators = HashSet<OneginiAuthenticator>()
        val oneginiAuthenticator = mockkClass(OneginiAuthenticator::class, relaxed = true)
        every { oneginiAuthenticator.type } returns OneginiAuthenticator.FINGERPRINT
        authenticators.add(oneginiAuthenticator)
        every { userClient.getNotRegisteredAuthenticators(USER_PROFILE) } returns authenticators
        Utils.startClient(rNOneginiSdk, oneginiClient, null, true)

        val handlerSlot = slot<OneginiAuthenticatorDeregistrationHandler>()
        every {
            userClient.deregisterAuthenticator(oneginiAuthenticator, capture(handlerSlot))
        } answers {}

        val promise = mockkClass(Promise::class, relaxed = true)
        rNOneginiSdk.deregisterFingerprintAuthenticator(USER_PROFILE.profileId, promise)

        val returnMap = mockkClass(WritableMap::class, relaxed = true)

        mockkStatic(Arguments::class)
        every { Arguments.createMap() } returns returnMap

        handlerSlot.captured.onSuccess()

        verify { promise.resolve(null) }
    }

    @Test
    fun deregisterFingerprintAuthenticator_PROFILE_DOES_NOT_EXIST() {
        val oneginiClient = setupOneginiClient()
        every { userClient.userProfiles } returns emptySet()
        Utils.startClient(rNOneginiSdk, oneginiClient, null, true)

        val promise = mockkClass(Promise::class, relaxed = true)
        rNOneginiSdk.deregisterFingerprintAuthenticator("test", promise)
        every { promise.reject(OneginiWrapperErrors.PROFILE_DOES_NOT_EXIST.toString(), any<String>()) }
    }

    @Test
    fun deregisterFingerprintAuthenticator_AUTHENTICATOR_DOES_NOT_EXIST() {
        val oneginiClient = setupOneginiClient()

        val profiles = HashSet<UserProfile>()
        profiles.add(USER_PROFILE)
        every { userClient.userProfiles } returns profiles
        every { userClient.getRegisteredAuthenticators(USER_PROFILE) } returns emptySet()
        Utils.startClient(rNOneginiSdk, oneginiClient, null, true)

        val promise = mockkClass(Promise::class, relaxed = true)
        rNOneginiSdk.deregisterFingerprintAuthenticator(USER_PROFILE.profileId, promise)
        every { promise.reject(OneginiWrapperErrors.AUTHENTICATOR_DOES_NOT_EXIST.toString(), any<String>()) }
    }


    @Test
    fun isFingerprintAuthenticatorRegistered_PROFILE_DOES_NOT_EXIST() {
        val oneginiClient = setupOneginiClient()
        every { userClient.userProfiles } returns emptySet()
        Utils.startClient(rNOneginiSdk, oneginiClient, null, true)

        val promise = mockkClass(Promise::class, relaxed = true)
        rNOneginiSdk.isFingerprintAuthenticatorRegistered("test", promise)
        every { promise.reject(OneginiWrapperErrors.PROFILE_DOES_NOT_EXIST.toString(), any<String>()) }
    }

    @Test
    fun isFingerprintAuthenticatorRegistered_AUTHENTICATOR_DOES_NOT_EXIST() {
        val oneginiClient = setupOneginiClient()

        val profiles = HashSet<UserProfile>()
        profiles.add(USER_PROFILE)
        every { userClient.userProfiles } returns profiles
        every { userClient.getRegisteredAuthenticators(USER_PROFILE) } returns emptySet()
        Utils.startClient(rNOneginiSdk, oneginiClient, null, true)

        val promise = mockkClass(Promise::class, relaxed = true)
        rNOneginiSdk.isFingerprintAuthenticatorRegistered(USER_PROFILE.profileId, promise)
        every { promise.reject(OneginiWrapperErrors.AUTHENTICATOR_DOES_NOT_EXIST.toString(), any<String>()) }
    }

    @Test
    fun submitFingerprintAcceptAuthenticationRequest_successful() {
        val oneginiClient = mockkClass(OneginiClient::class)
        Utils.startClient(rNOneginiSdk, oneginiClient, null, true)

        setupStartAuthentication()

        val callback = mockkClass(OneginiFingerprintCallback::class, relaxed = true)
        OneginiComponents.oneginiSDK.fingerprintAuthenticationRequestHandler!!.startAuthentication(USER_PROFILE, callback)

        val promise = mockk<Promise>(relaxed = true)
        rNOneginiSdk.submitFingerprintAcceptAuthenticationRequest(promise)
        every { callback.acceptAuthenticationRequest() }
        verify { promise.resolve(any()) }
    }

    @Test
    fun submitFingerprintAcceptAuthenticationRequest_error() {
        val oneginiClient = mockkClass(OneginiClient::class)
        Utils.startClient(rNOneginiSdk, oneginiClient, null, false)

        setupStartAuthentication()

        val promise = mockkClass(Promise::class, relaxUnitFun = true)
        rNOneginiSdk.submitFingerprintAcceptAuthenticationRequest(promise)
        every { promise.reject(OneginiWrapperErrors.FINGERPRINT_IS_NOT_ENABLED.toString(), any<String>()) }
    }

    @Test
    fun submitFingerprintDenyAuthenticationRequest_successful() {
        val oneginiClient = mockkClass(OneginiClient::class)
        Utils.startClient(rNOneginiSdk, oneginiClient, null, true)

        setupStartAuthentication()

        val callback = mockkClass(OneginiFingerprintCallback::class, relaxed = true)
        OneginiComponents.oneginiSDK.fingerprintAuthenticationRequestHandler!!.startAuthentication(USER_PROFILE, callback)

        val promise = mockkClass(Promise::class, relaxed = true)
        rNOneginiSdk.submitFingerprintDenyAuthenticationRequest(promise)
        every { callback.acceptAuthenticationRequest() }
        verify { promise.resolve(any()) }
    }

    @Test
    fun submitFingerprintDenyAuthenticationRequest_error() {
        val oneginiClient = mockkClass(OneginiClient::class)
        Utils.startClient(rNOneginiSdk, oneginiClient, null, false)

        setupStartAuthentication()

        val promise = mockkClass(Promise::class, relaxUnitFun = true)
        rNOneginiSdk.submitFingerprintDenyAuthenticationRequest(promise)
        every { promise.reject(OneginiWrapperErrors.FINGERPRINT_IS_NOT_ENABLED.toString(), any<String>()) }
    }

    @Test
    fun submitFingerprintFallbackToPin_successful() {
        val oneginiClient = mockkClass(OneginiClient::class)
        Utils.startClient(rNOneginiSdk, oneginiClient, null, true)

        setupStartAuthentication()

        val callback = mockkClass(OneginiFingerprintCallback::class, relaxed = true)
        OneginiComponents.oneginiSDK.fingerprintAuthenticationRequestHandler!!.startAuthentication(USER_PROFILE, callback)

        val promise = mockkClass(Promise::class, relaxed = true)
        rNOneginiSdk.submitFingerprintFallbackToPin(promise)
        every { callback.acceptAuthenticationRequest() }
        verify { promise.resolve(any()) }
    }

    @Test
    fun submitFingerprintFallbackToPin_error() {
        val oneginiClient = mockkClass(OneginiClient::class)
        Utils.startClient(rNOneginiSdk, oneginiClient, null, false)

        setupStartAuthentication()

        val promise = mockkClass(Promise::class, relaxUnitFun = true)
        rNOneginiSdk.submitFingerprintFallbackToPin(promise)
        every { promise.reject(OneginiWrapperErrors.FINGERPRINT_IS_NOT_ENABLED.toString(), any<String>()) }
    }

    fun setupOneginiClient(): OneginiClient {
        val oneginiClient = mockkClass(OneginiClient::class)
        every { oneginiClient.userClient } returns userClient

        return oneginiClient
    }

    fun setupStartAuthentication() {
        val returnMap = mockkClass(WritableMap::class, relaxed = true)
        mockkStatic(Arguments::class)
        every { Arguments.createMap() } returns returnMap

        val emitNameSlot = slot<String>()
        val emitDataSlot = slot<Any>()
        every {
            rCTDeviceEventEmitter.emit(capture(emitNameSlot), capture(emitDataSlot))
        } answers {}
    }
}