import android.content.Context
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.onegini.mobile.OneginiComponets
import com.onegini.mobile.RNOneginiSdk
import com.onegini.mobile.exception.OneginReactNativeException
import com.onegini.mobile.sdk.android.client.OneginiClient
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiFingerprintCallback
import com.onegini.mobile.sdk.android.model.entity.UserProfile
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
    fun submitFingerprintAcceptAuthenticationRequest_successful() {
        val oneginiClient = mockkClass(OneginiClient::class)
        Utils.startClient(rNOneginiSdk, oneginiClient, null, true)

        setupStartAuthentication()

        val callback = mockkClass(OneginiFingerprintCallback::class, relaxed = true)
        OneginiComponets.oneginiSDK.fingerprintAuthenticationRequestHandler!!.startAuthentication(USER_PROFILE, callback)

        val promise = mockkClass(Promise::class)
        rNOneginiSdk.submitFingerprintAcceptAuthenticationRequest(promise)
        every { callback.acceptAuthenticationRequest() }
    }

    @Test
    fun submitFingerprintAcceptAuthenticationRequest_error() {
        val oneginiClient = mockkClass(OneginiClient::class)
        Utils.startClient(rNOneginiSdk, oneginiClient, null, false)

        setupStartAuthentication()

        val promise = mockkClass(Promise::class, relaxUnitFun = true)
        rNOneginiSdk.submitFingerprintAcceptAuthenticationRequest(promise)
        every { promise.reject(OneginReactNativeException.FINGERPRINT_IS_NOT_ENABLED.toString(), any<String>()) }
    }

    @Test
    fun submitFingerprintDenyAuthenticationRequest_successful() {
        val oneginiClient = mockkClass(OneginiClient::class)
        Utils.startClient(rNOneginiSdk, oneginiClient, null, true)

        setupStartAuthentication()

        val callback = mockkClass(OneginiFingerprintCallback::class, relaxed = true)
        OneginiComponets.oneginiSDK.fingerprintAuthenticationRequestHandler!!.startAuthentication(USER_PROFILE, callback)

        val promise = mockkClass(Promise::class)
        rNOneginiSdk.submitFingerprintDenyAuthenticationRequest(promise)
        every { callback.acceptAuthenticationRequest() }
    }

    @Test
    fun submitFingerprintDenyAuthenticationRequest_error() {
        val oneginiClient = mockkClass(OneginiClient::class)
        Utils.startClient(rNOneginiSdk, oneginiClient, null, false)

        setupStartAuthentication()

        val promise = mockkClass(Promise::class, relaxUnitFun = true)
        rNOneginiSdk.submitFingerprintDenyAuthenticationRequest(promise)
        every { promise.reject(OneginReactNativeException.FINGERPRINT_IS_NOT_ENABLED.toString(), any<String>()) }
    }

    @Test
    fun submitFingerprintFallbackToPin_successful() {
        val oneginiClient = mockkClass(OneginiClient::class)
        Utils.startClient(rNOneginiSdk, oneginiClient, null, true)

        setupStartAuthentication()

        val callback = mockkClass(OneginiFingerprintCallback::class, relaxed = true)
        OneginiComponets.oneginiSDK.fingerprintAuthenticationRequestHandler!!.startAuthentication(USER_PROFILE, callback)

        val promise = mockkClass(Promise::class)
        rNOneginiSdk.submitFingerprintFallbackToPin(promise)
        every { callback.acceptAuthenticationRequest() }
    }

    @Test
    fun submitFingerprintFallbackToPin_error() {
        val oneginiClient = mockkClass(OneginiClient::class)
        Utils.startClient(rNOneginiSdk, oneginiClient, null, false)

        setupStartAuthentication()

        val promise = mockkClass(Promise::class, relaxUnitFun = true)
        rNOneginiSdk.submitFingerprintFallbackToPin(promise)
        every { promise.reject(OneginReactNativeException.FINGERPRINT_IS_NOT_ENABLED.toString(), any<String>()) }
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