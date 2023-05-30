package com.onegini.mobile.sdk.reactnative

import com.facebook.react.bridge.JavaOnlyMap
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import com.onegini.mobile.sdk.reactnative.clean.use_cases.ResourceRequestUseCase
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError.*
import com.onegini.mobile.sdk.reactnative.exception.REQUEST_METHOD_NOT_SUPPORTED
import com.onegini.mobile.sdk.reactnative.exception.REQUEST_MISSING_PATH_PARAMETER
import com.onegini.mobile.sdk.reactnative.exception.REQUEST_TYPE_NOT_SUPPORTED
import okhttp3.Callback
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.io.IOException


@RunWith(MockitoJUnitRunner::class)
class ResourceRequestUseCaseTests {

  @get:Rule
  val reactArgumentsTestRule = ReactArgumentsTestRule()

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  lateinit var oneginiSdk: OneginiSDK

  @Mock
  lateinit var promiseMock: Promise

  @Mock
  lateinit var okHttpClientMock: OkHttpClient

  @Mock
  lateinit var okhttp3CallMock: okhttp3.Call

  @Mock
  lateinit var ioExceptionMock: IOException

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  lateinit var responseMock: Response

  lateinit var resourceRequestUseCase: ResourceRequestUseCase

  @Before
  fun setup() {
    // FIXME: RNP-138 Remove this, as this is to prevent 'null' being added before baseUrl
    whenever(oneginiSdk.oneginiClient.configModel.resourceBaseUrl).thenReturn("")
    resourceRequestUseCase = ResourceRequestUseCase(oneginiSdk)
    whenAccessTokenExists()
  }

  private val correctUrl = "https://example.url/resources/test"
  private val incorrectUrl = "example.url/resources/test"
  private val headerOne = Pair("header1", "headerValue1")
  private val headerTwo = Pair("header2", "headerValue2")

  @Test
  fun `When calling with a malformed URL, Then should reject with a PARAMETERS_NOT_CORRECT and a message containing the error`() {
    val details = JavaOnlyMap()
    details.putString("path", incorrectUrl)
    details.putString("method", "GET")
    resourceRequestUseCase("User", details, promiseMock)
    // Sadly we can't access the error string from okHttp, so we'll just have to check if it rejects with a String.
    verify(promiseMock).reject(eq(PARAMETERS_NOT_CORRECT.code.toString()), anyString())

  }

  @Test
  fun `When calling with a non supported 'type' String but further correct data, Then should reject with PARAMETERS_NOT_CORRECT error`() {
    resourceRequestUseCase("unsupportedType", requiredDetailsGET(), promiseMock)
    verify(promiseMock).reject(PARAMETERS_NOT_CORRECT.code.toString(), REQUEST_TYPE_NOT_SUPPORTED)
  }

  @Test
  fun `When calling with a details object missing it's path parameter, Then should reject with PARAMETERS_NOT_CORRECT error`() {
    val details = JavaOnlyMap()
    details.putString("method", "GET")
    resourceRequestUseCase("User", details, promiseMock)
    verify(promiseMock).reject(PARAMETERS_NOT_CORRECT.code.toString(), REQUEST_MISSING_PATH_PARAMETER)
  }

  @Test
  fun `When calling with a details object containing an unsupported 'method' parameter, Then should reject with PARAMETERS_NOT_CORRECT error`() {
    val details = JavaOnlyMap()
    details.putString("method", "unsupportedMethod")
    details.putString("path", correctUrl)
    resourceRequestUseCase("User", details, promiseMock)
    verify(promiseMock).reject(PARAMETERS_NOT_CORRECT.code.toString(), REQUEST_METHOD_NOT_SUPPORTED)
  }

  @Test
  fun `When calling with a details object containing headers, Then it should attempt to perform the resource call with only the headers that are Strings`() {
    val headers = correctHeaders()
    // We're adding a map to the correct headers here, this map in the headers should get ignored by the resourceRequest
    val wrongHeader = JavaOnlyMap()
    wrongHeader.putString("0", "0")
    headers.putMap("header3", wrongHeader)

    val details = requiredDetailsGET()
    details.putMap("headers", headers)
    whenever(oneginiSdk.oneginiClient.userClient.resourceOkHttpClient).thenReturn(okHttpClientMock)
    argumentCaptor<Request> {
      whenever(
        okHttpClientMock.newCall(capture())
      ).thenAnswer {
        assertEquals(firstValue.headers.size, 2)
        assertEquals(firstValue.headers[headerOne.first], headerOne.second)
        assertEquals(firstValue.headers[headerTwo.first], headerTwo.second)
        assert(firstValue.headers["header3"] == null)
        okhttp3CallMock
      }
    }
    resourceRequestUseCase("User", details, promiseMock)
    verify(okHttpClientMock).newCall(any())

  }

  @Test
  fun `When calling with correct data, Then it should attempt to perform the resource call with that data`() {
    val headers = correctHeaders()
    val details = requiredDetailsGET()
    details.putMap("headers", headers)
    whenever(oneginiSdk.oneginiClient.userClient.resourceOkHttpClient).thenReturn(okHttpClientMock)
    argumentCaptor<Request> {
      whenever(
        okHttpClientMock.newCall(capture())
      ).thenAnswer {
        assertEquals(firstValue.headers[headerOne.first], headerOne.second)
        assertEquals(firstValue.headers[headerTwo.first], headerTwo.second)
        assertEquals(firstValue.url.toString(), correctUrl)
        // FIXME: RNP-140: Add body check here
        okhttp3CallMock
      }
    }
    resourceRequestUseCase("User", details, promiseMock)
    verify(okHttpClientMock).newCall(any())

  }

  @Test
  fun `When the resourceCall calls onResponse, Then it should resolve with a map containing the response body,headers,status,ok`() {
    val headers = correctHeaders()
    val details = requiredDetailsGET()
    setupResponseMock()
    details.putMap("headers", headers)
    whenever(oneginiSdk.oneginiClient.userClient.resourceOkHttpClient).thenReturn(okHttpClientMock)
    whenever(okHttpClientMock.newCall(any())).thenReturn(okhttp3CallMock)
    argumentCaptor<Callback> {
      whenever(
        okhttp3CallMock.enqueue(capture())
      ).thenAnswer {
        firstValue.onResponse(okhttp3CallMock, responseMock)
      }
    }
    resourceRequestUseCase("User", details, promiseMock)
    argumentCaptor<ReadableMap> {
      verify(promiseMock).resolve(capture())
      assertEquals(firstValue.getString("body"), responseMock.body?.string())
      assertEquals(firstValue.getInt("status"), responseMock.code)
      assertEquals(firstValue.getBoolean("ok"), responseMock.isSuccessful)
      val responseHeaders = firstValue.getMap("headers")
      assertEquals(responseHeaders!!.getString(headerOne.first), headerOne.second)
    }
  }

  @Test
  fun `When the resourceCall calls onFailure, Then it should reject the promise with the error`() {
    val headers = correctHeaders()
    val details = requiredDetailsGET()
    details.putMap("headers", headers)
    whenever(oneginiSdk.oneginiClient.userClient.resourceOkHttpClient).thenReturn(okHttpClientMock)
    whenever(okHttpClientMock.newCall(any())).thenReturn(okhttp3CallMock)
    argumentCaptor<Callback> {
      whenever(
        okhttp3CallMock.enqueue(capture())
      ).thenAnswer {
        firstValue.onFailure(okhttp3CallMock, ioExceptionMock)
      }
    }
    resourceRequestUseCase("User", details, promiseMock)
    verify(promiseMock).reject(RESOURCE_CALL_ERROR.code.toString(), ioExceptionMock.message)
  }

  @Test
  fun `When performing authenticated resourceCall with correct data but no acccessToken, Then should reject with USER_NOT_AUTHENTICATED`() {
    whenAccessTokenDoesNotExist()
    val headers = correctHeaders()
    val details = requiredDetailsGET()
    details.putMap("headers", headers)
    resourceRequestUseCase("User", details, promiseMock)
    verify(promiseMock).reject(USER_NOT_AUTHENTICATED.code.toString(), USER_NOT_AUTHENTICATED.message)
  }

  private fun requiredDetailsGET(): JavaOnlyMap {
    val details = JavaOnlyMap()
    details.putString("path", correctUrl)
    details.putString("method", "GET")
    return details
  }

  private fun correctHeaders(): JavaOnlyMap {
    val headers = JavaOnlyMap()
    headers.putString(headerOne.first, headerOne.second)
    headers.putString(headerTwo.first, headerTwo.second)
    return headers
  }

  private fun setupResponseMock() {
    whenever(responseMock.body?.string()).thenReturn("responseBody")
    val headers = Headers.Builder().add(headerOne.first, headerOne.second).build()
    whenever(responseMock.headers).thenReturn(headers)
  }

  private fun whenAccessTokenExists() {
    whenever(oneginiSdk.oneginiClient.accessToken).thenReturn("nonNullAccessToken")
  }

  private fun whenAccessTokenDoesNotExist() {
    whenever(oneginiSdk.oneginiClient.accessToken).thenReturn(null)
  }
}
