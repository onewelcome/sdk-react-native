import android.content.Context
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.onegini.mobile.OneginiComponets
import com.onegini.mobile.RNOneginiSdk
import com.onegini.mobile.exception.OneginReactNativeException
import com.onegini.mobile.sdk.android.client.OneginiClient
import com.onegini.mobile.sdk.android.client.UserClient
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticatorDeregistrationHandler
import com.onegini.mobile.sdk.android.handlers.OneginiAuthenticatorRegistrationHandler
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiFingerprintCallback
import com.onegini.mobile.sdk.android.model.OneginiAuthenticator
import com.onegini.mobile.sdk.android.model.entity.CustomInfo
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test


class RNOneginiSdkAuthenticatorTest {

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

        val profiles = HashSet<UserProfile>()
        profiles.add(USER_PROFILE)
        every { userClient.userProfiles } returns profiles
    }

    @Test
    fun getAllAuthenticators_PROFILE_DOES_NOT_EXIST() {
        val oneginiClient = setupOneginiClient()
        every { userClient.userProfiles } returns emptySet()
        Utils.startClient(rNOneginiSdk, oneginiClient, null, true)

        val promise = mockkClass(Promise::class, relaxed = true)
        rNOneginiSdk.getAllAuthenticators("test", promise)
        verify { promise.reject(OneginReactNativeException.PROFILE_DOES_NOT_EXIST.toString(), any<String>()) }
    }

    @Test
    fun getRegisteredAuthenticators_PROFILE_DOES_NOT_EXIST() {
        val oneginiClient = setupOneginiClient()
        every { userClient.userProfiles } returns emptySet()
        Utils.startClient(rNOneginiSdk, oneginiClient, null, true)

        val promise = mockkClass(Promise::class, relaxed = true)
        rNOneginiSdk.getRegisteredAuthenticators("test", promise)
        verify { promise.reject(OneginReactNativeException.PROFILE_DOES_NOT_EXIST.toString(), any<String>()) }
    }

    @Test
    fun getAllAuthenticators_successful() {
        val oneginiClient = setupOneginiClient()
        Utils.startClient(rNOneginiSdk, oneginiClient, null, true)

        val authenticators = HashSet<OneginiAuthenticator>()
        val oneginiAuthenticator = mockkClass(OneginiAuthenticator::class, relaxed = true)
        every { oneginiAuthenticator.id } returns "id"
        every { oneginiAuthenticator.name } returns "name"

        every { oneginiAuthenticator.type } returns OneginiAuthenticator.FINGERPRINT
        authenticators.add(oneginiAuthenticator)
        every { userClient.getAllAuthenticators(USER_PROFILE) } returns authenticators

        val returnMap = mockkClass(WritableMap::class, relaxed = true)

        mockkStatic(Arguments::class)
        every { Arguments.createMap() } returns returnMap

        val array = mockkClass(WritableArray::class, relaxed = true)
        every { Arguments.createArray() } returns array

        val promise = mockkClass(Promise::class, relaxed = true)
        rNOneginiSdk.getAllAuthenticators(USER_PROFILE.profileId, promise)

        verify {returnMap.putString("id", "id")}
        verify {returnMap.putString("name", "name")}
        verify {returnMap.putInt("type", OneginiAuthenticator.FINGERPRINT)}
        verify {returnMap.putBoolean("isPreferred", false)}
        verify {returnMap.putBoolean("isRegistered", false)}
        verify { promise.resolve(any()) }
    }

    @Test
    fun getRegisteredAuthenticators_successful() {
        val oneginiClient = setupOneginiClient()
        Utils.startClient(rNOneginiSdk, oneginiClient, null, true)

        val authenticators = HashSet<OneginiAuthenticator>()
        val oneginiAuthenticator = mockkClass(OneginiAuthenticator::class, relaxed = true)
        every { oneginiAuthenticator.id } returns "id"
        every { oneginiAuthenticator.name } returns "name"

        every { oneginiAuthenticator.type } returns OneginiAuthenticator.FINGERPRINT
        authenticators.add(oneginiAuthenticator)
        every { userClient.getRegisteredAuthenticators(USER_PROFILE) } returns authenticators

        val returnMap = mockkClass(WritableMap::class, relaxed = true)

        mockkStatic(Arguments::class)
        every { Arguments.createMap() } returns returnMap

        val array = mockkClass(WritableArray::class, relaxed = true)
        every { Arguments.createArray() } returns array

        val promise = mockkClass(Promise::class, relaxed = true)
        rNOneginiSdk.getRegisteredAuthenticators(USER_PROFILE.profileId, promise)

        verify {returnMap.putString("id", "id")}
        verify {returnMap.putString("name", "name")}
        verify {returnMap.putInt("type", OneginiAuthenticator.FINGERPRINT)}
        verify {returnMap.putBoolean("isPreferred", false)}
        verify {returnMap.putBoolean("isRegistered", false)}
        verify { promise.resolve(any()) }
    }

    fun setupOneginiClient(): OneginiClient {
        val oneginiClient = mockkClass(OneginiClient::class)
        every { oneginiClient.userClient } returns userClient

        return oneginiClient
    }
}