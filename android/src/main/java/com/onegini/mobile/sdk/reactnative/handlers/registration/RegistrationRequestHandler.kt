package com.onegini.mobile.sdk.reactnative.handlers.registration

import android.net.Uri
import com.onegini.mobile.sdk.android.handlers.request.OneginiBrowserRegistrationRequestHandler
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiBrowserRegistrationCallback
import com.onegini.mobile.sdk.reactnative.exception.CANCEL_BROWSER_REGISTRATION_NOT_ALLOWED
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError.ACTION_NOT_ALLOWED
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperError.REGISTRATION_NOT_IN_PROGRESS
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegistrationRequestHandler @Inject constructor(private val eventEmitter: RegistrationEventEmitter) :
  OneginiBrowserRegistrationRequestHandler {

  private var callback: OneginiBrowserRegistrationCallback? = null

  /**
   * Finish registration action with result from web browser
   */
  fun handleRegistrationCallback(uri: Uri?) {
    callback?.let { registrationCallback ->
      registrationCallback.handleRegistrationCallback(uri)
      callback = null
    } ?: throw OneginiReactNativeException(REGISTRATION_NOT_IN_PROGRESS)
  }

  /**
   * Cancel registration action in case of web browser error
   */
  fun cancelRegistration() {
    callback?.let { registrationCallback ->
      registrationCallback.denyRegistration()
      callback = null
    } ?: throw OneginiReactNativeException(ACTION_NOT_ALLOWED.code, CANCEL_BROWSER_REGISTRATION_NOT_ALLOWED)
  }

  override fun startRegistration(uri: Uri, oneginiBrowserRegistrationCallback: OneginiBrowserRegistrationCallback) {
    callback = oneginiBrowserRegistrationCallback
    eventEmitter.onSendUrl(uri);
  }
}
