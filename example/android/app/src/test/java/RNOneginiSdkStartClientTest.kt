import android.content.Context
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.RNOneginiSdk
import com.onegini.mobile.sdk.android.client.OneginiClient
import com.onegini.mobile.sdk.android.client.OneginiClientBuilder
import com.onegini.mobile.sdk.android.handlers.request.OneginiCreatePinRequestHandler
import com.onegini.mobile.sdk.android.handlers.request.OneginiPinAuthenticationRequestHandler
import com.onegini.mobile.view.handlers.CreatePinRequestHandler
import com.onegini.mobile.view.handlers.PinAuthenticationRequestHandler
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner


@RunWith(PowerMockRunner::class)
@PrepareForTest(OneginiSDK::class)
class RNOneginiSdkStartClientTest {

    private lateinit var rNOneginiSdk: RNOneginiSdk
    val reactApplicationContext = PowerMockito.mock(ReactApplicationContext::class.java)
    val appContext = PowerMockito.mock(Context::class.java)
    val promise = PowerMockito.mock(Promise::class.java)
    val oneginiClientBuilder = PowerMockito.mock(OneginiClientBuilder::class.java)

    @Before
    fun setup() {
        `when`(reactApplicationContext.applicationContext).thenReturn(appContext)
        rNOneginiSdk = RNOneginiSdk(reactApplicationContext)
    }

    @Test
    fun startClient_Config() {
//        val map = Arguments.createMap()
//        map.putString("configModelClassName", "testConfigModelClassName")
//        map.putString("securityControllerClassName", "testSecurityControllerClassName")
//        map.putString("securityControllerClassName", "testSecurityControllerClassName")
//        map.putBoolean("enableMobileAuthenticationOtp", true)
//        map.putBoolean("enableFingerprint", false)
//        val array = Arguments.createArray()
//        array.pushMap(createProvider("idTest", true))
//        map.putArray("customProviders", array)
//        Mockito.spy<Class<EmpService>>(EmpService::class.java)
        val config = PowerMockito.mock(ReadableMap::class.java)
        PowerMockito.`when`(config.getString("configModelClassName")).thenReturn("testConfigModelClassName")
        PowerMockito.`when`(config.getString("securityControllerClassName")).thenReturn("testSecurityControllerClassName")
        PowerMockito.`when`(config.getBoolean("enableMobileAuthenticationOtp")).thenReturn(true)
        PowerMockito.`when`(config.getBoolean("enableFingerprint")).thenReturn(true)

        val oneginiClient = PowerMockito.mock(OneginiClient::class.java)

        PowerMockito.`when`(oneginiClientBuilder.setHttpConnectTimeout(anyInt())).thenReturn(oneginiClientBuilder)
        PowerMockito.`when`(oneginiClientBuilder.setHttpReadTimeout(anyInt())).thenReturn(oneginiClientBuilder)
        PowerMockito.`when`(oneginiClientBuilder.build()).thenReturn(oneginiClient)


//        val oneginiClient = OneginiClient()
//        PowerMockito.spy(oneginiClient)

        PowerMockito.whenNew(OneginiClientBuilder::class.java)
                //.withNoArguments()
                .withArguments(any(Context::class.java),any(CreatePinRequestHandler::class.java), any(PinAuthenticationRequestHandler::class.java))
                .thenReturn(oneginiClientBuilder)


        rNOneginiSdk.startClient(config, promise)

        assertEquals("Karol", "gg")
    }


    fun createProvider(id: String, isTwoStep: Boolean): ReadableMap {
        val map = Arguments.createMap()
        map.putString("id", id)
        map.putBoolean("isTwoStep", isTwoStep)
        return map
    }

//    rnConfig.getString("configModelClassName"),
//    rnConfig.getString("securityControllerClassName"),
//    toReactNativeIdentityProviderList(rnConfig.getArray("customProviders")),
//    rnConfig.getBoolean("enableMobileAuthenticationOtp"),
//    rnConfig.getBoolean("enableFingerprint")
}