package com.onegini.mobile

import com.facebook.react.bridge.JavaOnlyMap
import com.onegini.mobile.clean.use_cases.ResourceRequestUseCase
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.lenient
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class ResourceRequestUseCaseTests : BaseTests() {

    @Mock
    lateinit var resourceOkHttpClient: OkHttpClient

    @Mock
    lateinit var implicitResourceOkHttpClient: OkHttpClient

    @Mock
    lateinit var anonymousResourceOkHttpClient: OkHttpClient

    @Before
    fun prepareMocks() {
        lenient().`when`(userClient.resourceOkHttpClient).thenReturn(resourceOkHttpClient)
        lenient().`when`(userClient.implicitResourceOkHttpClient).thenReturn(implicitResourceOkHttpClient)
        lenient().`when`(deviceClient.anonymousResourceOkHttpClient).thenReturn(anonymousResourceOkHttpClient)

        `when`(oneginiClient.configModel).thenReturn(TestData.configModel)

        RxAndroidPlugins.reset()
        RxJavaPlugins.reset()

        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    //

    @Test
    fun `when called with User type should create and call proper request`() {
        val map = JavaOnlyMap()
        map.putString("method", "get")
        map.putString("path", "resourcesTest")
        map.putString("encoding", "testEncoding")
        map.putString("body", "testBody")
        val headers = JavaOnlyMap()
        headers.putString("header1", "testVal")
        map.putMap("headers", headers)

        ResourceRequestUseCase()("User", map, promiseMock)

        argumentCaptor<Request> {
            verify(resourceOkHttpClient).newCall(capture())

            Assert.assertEquals("GET", firstValue.method())
            Assert.assertEquals("testVal", firstValue.header("header1"))
            Assert.assertEquals(null, firstValue.body())
        }
    }

    @Test
    fun `when called with ImplicitUser type should create and call proper request`() {
        val map = JavaOnlyMap()
        map.putString("method", "POST")
        map.putString("path", "resourcesTest")
        map.putString("body", "testBody")
        val headers = JavaOnlyMap()
        headers.putString("header1", "testVal")
        map.putMap("headers", headers)

        ResourceRequestUseCase()("ImplicitUser", map, promiseMock)

        argumentCaptor<Request> {
            verify(implicitResourceOkHttpClient).newCall(capture())

            Assert.assertEquals("POST", firstValue.method())
            Assert.assertEquals("testVal", firstValue.header("header1"))
        }
    }

    @Test
    fun `when called with Anonymous type should create and call proper request`() {
        val map = JavaOnlyMap()
        map.putString("method", "PUT")
        map.putString("path", "resourcesTest")
        map.putString("body", "testBody")
        val headers = JavaOnlyMap()
        headers.putString("header1", "testVal")
        map.putMap("headers", headers)

        ResourceRequestUseCase()("Anonymous", map, promiseMock)

        argumentCaptor<Request> {
            verify(anonymousResourceOkHttpClient).newCall(capture())

            Assert.assertEquals("PUT", firstValue.method())
            Assert.assertEquals("testVal", firstValue.header("header1"))
        }
    }

    @Test
    fun `when successful should resolve with response`() {
        val map = JavaOnlyMap()
        map.putString("method", "PUT")
        map.putString("path", "resourcesTest")
        map.putString("body", "testBody")
        val headers = JavaOnlyMap()
        headers.putString("header1", "testVal")
        map.putMap("headers", headers)

        ResourceRequestUseCase()("Anonymous", map, promiseMock)

        // TODO: What would be the easiest way to mock success (observer) here?

        argumentCaptor<String> {
            verify(promiseMock).resolve(capture())
        }
    }
}
