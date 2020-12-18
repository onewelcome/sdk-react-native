import Utils.createProvider
import android.content.Context
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.onegini.mobile.OneginiComponets
import com.onegini.mobile.RNOneginiSdk
import com.onegini.mobile.sdk.android.client.OneginiClient
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiCustomRegistrationCallback
import com.onegini.mobile.sdk.android.model.entity.CustomInfo
import com.onegini.mobile.view.handlers.customregistration.SimpleCustomRegistrationActionImpl
import com.onegini.mobile.view.handlers.customregistration.SimpleCustomTwoStepRegistrationActionImpl
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.*


class RNOneginiSdkCustomRegistrationTest {

    companion object {
        const val PROVIDER_ID = "PROVIDER_ID"
        const val RESULT = "RESULT"
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
    fun customRegistrationNotification_isTwoStep_false_finishRegistration() {

        val emitNameSlot = slot<String>()
        val emitDataSlot = slot<Any>()
        every {
            rCTDeviceEventEmitter.emit(capture(emitNameSlot), capture(emitDataSlot))
        } answers {}

        val arrayProviders = ArrayList<Any>()
        arrayProviders.add(createProvider(PROVIDER_ID, false))

        val oneginiClient = mockkClass(OneginiClient::class)
        Utils.startClient(rNOneginiSdk, oneginiClient, arrayProviders)

        val actions = OneginiComponets.oneginiSDK.simpleCustomRegistrationActions
        assertEquals(1, actions.size)
        assertEquals(PROVIDER_ID, actions[0].getIdProvider())
        assertEquals(true, actions[0] is SimpleCustomRegistrationActionImpl)

        val returnMap = mockkClass(WritableMap::class, relaxed = true)
        val customInfoSlot = slot<WritableMap>()
        every {
            returnMap.putMap("customInfo", capture(customInfoSlot))
        } answers {}

        mockkStatic(Arguments::class)
        every { Arguments.createMap() } returns returnMap

        val calback = mockkClass(OneginiCustomRegistrationCallback::class, relaxed = true)
        val customInfo = CustomInfo(10, "data")
        (actions[0] as SimpleCustomRegistrationActionImpl).finishRegistration(calback, customInfo)

        assertEquals("ONEGINI_CUSTOM_REGISTRATION_NOTIFICATION", emitNameSlot.captured)
        assertEquals(returnMap, emitDataSlot.captured)
        verify { returnMap.putString("identityProviderId", PROVIDER_ID) }
        verify { returnMap.putString("action", "finishRegistration") }
        verify { customInfoSlot.captured.putString("data", customInfo.data) }
        verify { customInfoSlot.captured.putInt("status", customInfo.status) }

        val promise = mockkClass(Promise::class, relaxed = true)
        rNOneginiSdk.submitCustomRegistrationReturnSuccess(PROVIDER_ID, RESULT, promise)
        verify { calback.returnSuccess(RESULT) }
    }

    @Test
    fun customRegistrationNotification_isTwoStep_true_initRegistration() {

        val emitNameSlot = slot<String>()
        val emitDataSlot = slot<Any>()
        every {
            rCTDeviceEventEmitter.emit(capture(emitNameSlot), capture(emitDataSlot))
        } answers {}

        val arrayProviders = ArrayList<Any>()
        arrayProviders.add(createProvider(PROVIDER_ID, true))

        val oneginiClient = mockkClass(OneginiClient::class)
        Utils.startClient(rNOneginiSdk, oneginiClient, arrayProviders)

        val actions = OneginiComponets.oneginiSDK.simpleCustomRegistrationActions
        assertEquals(1, actions.size)
        assertEquals(PROVIDER_ID, actions[0].getIdProvider())
        assertEquals(true, actions[0] is SimpleCustomTwoStepRegistrationActionImpl)

        val returnMap = mockkClass(WritableMap::class, relaxed = true)
        val customInfoSlot = slot<WritableMap>()
        every {
            returnMap.putMap("customInfo", capture(customInfoSlot))
        } answers {}

        mockkStatic(Arguments::class)
        every { Arguments.createMap() } returns returnMap

        val calback = mockkClass(OneginiCustomRegistrationCallback::class, relaxed = true)
        val customInfo = CustomInfo(10, "data")
        (actions[0] as SimpleCustomTwoStepRegistrationActionImpl).initRegistration(calback, customInfo)

        assertEquals("ONEGINI_CUSTOM_REGISTRATION_NOTIFICATION", emitNameSlot.captured)
        assertEquals(returnMap, emitDataSlot.captured)
        verify { returnMap.putString("identityProviderId", PROVIDER_ID) }
        verify { returnMap.putString("action", "initRegistration") }
        verify { customInfoSlot.captured.putString("data", customInfo.data) }
        verify { customInfoSlot.captured.putInt("status", customInfo.status) }

        val promise = mockkClass(Promise::class, relaxed = true)
        rNOneginiSdk.submitCustomRegistrationReturnSuccess(PROVIDER_ID, RESULT, promise)
        verify { calback.returnSuccess(RESULT) }
    }

    @Test
    fun customRegistrationNotification_isTwoStep_true_finishRegistration() {

        val emitNameSlot = slot<String>()
        val emitDataSlot = slot<Any>()
        every {
            rCTDeviceEventEmitter.emit(capture(emitNameSlot), capture(emitDataSlot))
        } answers {}

        val arrayProviders = ArrayList<Any>()
        arrayProviders.add(createProvider(PROVIDER_ID, true))

        val oneginiClient = mockkClass(OneginiClient::class)
        Utils.startClient(rNOneginiSdk, oneginiClient, arrayProviders)

        val actions = OneginiComponets.oneginiSDK.simpleCustomRegistrationActions
        assertEquals(1, actions.size)
        assertEquals(PROVIDER_ID, actions[0].getIdProvider())
        assertEquals(true, actions[0] is SimpleCustomTwoStepRegistrationActionImpl)

        val returnMap = mockkClass(WritableMap::class, relaxed = true)
        val customInfoSlot = slot<WritableMap>()
        every {
            returnMap.putMap("customInfo", capture(customInfoSlot))
        } answers {}

        mockkStatic(Arguments::class)
        every { Arguments.createMap() } returns returnMap

        val calback = mockkClass(OneginiCustomRegistrationCallback::class, relaxed = true)
        val customInfo = CustomInfo(10, "data")
        (actions[0] as SimpleCustomTwoStepRegistrationActionImpl).finishRegistration(calback, customInfo)

        assertEquals("ONEGINI_CUSTOM_REGISTRATION_NOTIFICATION", emitNameSlot.captured)
        assertEquals(returnMap, emitDataSlot.captured)
        verify { returnMap.putString("identityProviderId", PROVIDER_ID) }
        verify { returnMap.putString("action", "finishRegistration") }
        verify { customInfoSlot.captured.putString("data", customInfo.data) }
        verify { customInfoSlot.captured.putInt("status", customInfo.status) }

        val promise = mockkClass(Promise::class, relaxed = true)
        rNOneginiSdk.submitCustomRegistrationReturnSuccess(PROVIDER_ID, RESULT, promise)
        verify { calback.returnSuccess(RESULT) }
    }
}