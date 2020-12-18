import Utils.createProvider
import android.content.Context
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.onegini.mobile.OneginiComponets
import com.onegini.mobile.RNOneginiSdk
import com.onegini.mobile.sdk.android.client.OneginiClient
import com.onegini.mobile.sdk.android.client.OneginiClientBuilder
import com.onegini.mobile.sdk.android.handlers.OneginiInitializationHandler
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.emptySet
import kotlin.collections.set


class RNOneginiSdkStartClientTest {

    private lateinit var rNOneginiSdk: RNOneginiSdk

    @MockK
    lateinit var reactApplicationContext: ReactApplicationContext

    @MockK
    lateinit var appContext: Context

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        every { reactApplicationContext.applicationContext } returns appContext
        every { appContext.applicationContext } returns appContext
        rNOneginiSdk = RNOneginiSdk(reactApplicationContext)
    }

    @Test
    fun startClient_Config() {
        val config = mockkClass(ReadableMap::class)
        val providers = mockkClass(ReadableArray::class)

        val arrayProviders = ArrayList<Any>()
        arrayProviders.add(createProvider("test", false))
        arrayProviders.add(createProvider("testIsTwoStep", true))
        every { providers.toArrayList() } returns arrayProviders

        every { config.getString("configModelClassName") } returns "testConfigModelClassName"
        every { config.getString("securityControllerClassName") } returns "testSecurityControllerClassName"
        every { config.getBoolean("enableMobileAuthenticationOtp") } returns true
        every { config.getBoolean("enableFingerprint") } returns true
        every { config.getArray("customProviders") } returns providers

        val oneginiClient = mockkClass(OneginiClient::class)
        mockkStatic(OneginiClient::class)
        every { OneginiClient.getInstance() } returns oneginiClient

        mockkConstructor(OneginiClientBuilder::class)
        every { anyConstructed<OneginiClientBuilder>().build() } returns oneginiClient

        val oneginiInitializationHandlerSlot = slot<OneginiInitializationHandler>()
        every {
            oneginiClient.start(capture(oneginiInitializationHandlerSlot))
        } answers {}

        val promise = mockk<Promise>(relaxed = true)
        rNOneginiSdk.startClient(config, promise)
        oneginiInitializationHandlerSlot.captured.onSuccess(emptySet())

        verify { promise.resolve(any()) }
        assertEquals("testConfigModelClassName", OneginiComponets.oneginiSDK.config.configModelClassName)
        assertEquals("testSecurityControllerClassName", OneginiComponets.oneginiSDK.config.securityControllerClassName)
        assertTrue(OneginiComponets.oneginiSDK.config.enableMobileAuthenticationOtp)
        assertTrue(OneginiComponets.oneginiSDK.config.enableFingerprint)

        assertEquals("test", OneginiComponets.oneginiSDK.config.identityProviders[0].id)
        assertFalse(OneginiComponets.oneginiSDK.config.identityProviders[0].isTwoStep)

        assertEquals("testIsTwoStep", OneginiComponets.oneginiSDK.config.identityProviders[1].id)
        assertTrue(OneginiComponets.oneginiSDK.config.identityProviders[1].isTwoStep)
    }

}