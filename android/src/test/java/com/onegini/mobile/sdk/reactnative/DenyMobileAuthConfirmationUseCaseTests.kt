import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiAcceptDenyCallback
import com.onegini.mobile.sdk.android.model.entity.OneginiMobileAuthenticationRequest
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.clean.use_cases.DenyMobileAuthConfirmationUseCase
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.MOBILE_AUTH_OTP_IS_DISABLED
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.MOBILE_AUTH_OTP_NOT_IN_PROGRESS
import com.onegini.mobile.sdk.reactnative.handlers.mobileauthotp.MobileAuthOtpRequestEventEmitter
import com.onegini.mobile.sdk.reactnative.handlers.mobileauthotp.MobileAuthOtpRequestHandler
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class DenyMobileAuthConfirmationUseCaseTests {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    lateinit var oneginiSdk: OneginiSDK

    @Mock
    lateinit var promiseMock: Promise

    @Mock
    lateinit var mobileAuthOtpRequestEventEmitter: MobileAuthOtpRequestEventEmitter

    @Mock
    lateinit var oneginiMobileAuthenticationRequest: OneginiMobileAuthenticationRequest

    @Mock
    lateinit var oneginiAcceptDenyCallback: OneginiAcceptDenyCallback

    lateinit var mobileAuthOtpRequestHandler: MobileAuthOtpRequestHandler

    lateinit var denyMobileAuthConfirmationUseCase: DenyMobileAuthConfirmationUseCase

    @Before
    fun setup() {
        mobileAuthOtpRequestHandler = MobileAuthOtpRequestHandler(mobileAuthOtpRequestEventEmitter)
        denyMobileAuthConfirmationUseCase = DenyMobileAuthConfirmationUseCase(oneginiSdk, mobileAuthOtpRequestHandler)
    }

    @Test
    fun `When mobile authentication with OTP is not enabled, Then the promise should reject with that error`() {
        disabledMobileAuthentication()
        denyMobileAuthConfirmationUseCase(promiseMock)
        verify(promiseMock).reject(MOBILE_AUTH_OTP_IS_DISABLED.code.toString(), MOBILE_AUTH_OTP_IS_DISABLED.message)
    }

    @Test
    fun `When mobile authentication with OTP is enabled and no mobile authentication is in progress, Then should reject with that error`() {
        enabledMobileAuthentication()
        denyMobileAuthConfirmationUseCase(promiseMock)
        verify(promiseMock).reject(MOBILE_AUTH_OTP_NOT_IN_PROGRESS.code.toString(), MOBILE_AUTH_OTP_NOT_IN_PROGRESS.message)
    }

    @Test
    fun `When mobile authentication with OTP is enabled and mobile authentication is in progress, Then should resolve with null`() {
        enabledMobileAuthentication()
        startMobileAuthentication()
        denyMobileAuthConfirmationUseCase(promiseMock)
        verify(promiseMock).resolve(null)
    }

    private fun startMobileAuthentication() {
        mobileAuthOtpRequestHandler.startAuthentication(oneginiMobileAuthenticationRequest, oneginiAcceptDenyCallback)
    }

    private fun disabledMobileAuthentication() {
        `when`(oneginiSdk.config.enableMobileAuthenticationOtp).thenReturn(false)
    }

    private fun enabledMobileAuthentication() {
        `when`(oneginiSdk.config.enableMobileAuthenticationOtp).thenReturn(true)
    }

}