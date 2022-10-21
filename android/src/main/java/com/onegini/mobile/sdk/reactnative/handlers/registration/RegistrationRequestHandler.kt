package com.onegini.mobile.sdk.reactnative.handlers.registration

import com.onegini.mobile.sdk.android.handlers.request.OneginiBrowserRegistrationRequestHandler
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiBrowserRegistrationCallback
import android.net.Uri

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
  fun cancelRegistration(): Boolean {
    callback?.let { registrationCallback ->
      registrationCallback.denyRegistration()
      callback = null
      return true
    }
    return false;
  }

  override fun startRegistration(uri: Uri, oneginiBrowserRegistrationCallback: OneginiBrowserRegistrationCallback) {
    callback = oneginiBrowserRegistrationCallback
    eventEmitter.onSendUrl(uri);
  }
}
