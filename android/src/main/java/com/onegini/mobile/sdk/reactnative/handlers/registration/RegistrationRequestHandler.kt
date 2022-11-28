package com.onegini.mobile.sdk.reactnative.handlers.registration

import com.onegini.mobile.sdk.android.handlers.request.OneginiBrowserRegistrationRequestHandler
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiBrowserRegistrationCallback
import android.net.Uri
import com.onegini.mobile.sdk.reactnative.exception.CANCEL_BROWSER_REGISTRATION_NOT_ALLOWED
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS

class RegistrationRequestHandler : OneginiBrowserRegistrationRequestHandler {
  private var callback: OneginiBrowserRegistrationCallback? = null
  private var eventEmitter = RegistrationEventEmitter()
  /**
   * Finish registration action with result from web browser
   */
  fun handleRegistrationCallback(uri: Uri?): Boolean {
    callback?.let { registrationCallback ->
      registrationCallback.handleRegistrationCallback(uri)
      callback = null
      return true
    }
    return false;
  }

  /**
   * Cancel registration action in case of web browser error
   */
  fun cancelRegistration() {
    callback?.let { registrationCallback ->
      registrationCallback.denyRegistration()
      callback = null
    } ?: throw OneginiReactNativeException(OneginiWrapperErrors.ACTION_NOT_ALLOWED.code, CANCEL_BROWSER_REGISTRATION_NOT_ALLOWED)
  }

  override fun startRegistration(uri: Uri, oneginiBrowserRegistrationCallback: OneginiBrowserRegistrationCallback) {
    callback = oneginiBrowserRegistrationCallback
    eventEmitter.onSendUrl(uri);
  }
}
