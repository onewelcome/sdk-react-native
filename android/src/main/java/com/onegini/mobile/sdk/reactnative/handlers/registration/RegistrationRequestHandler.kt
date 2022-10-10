//@todo Later will be transferred to RN Wrapper
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
  fun handleRegistrationCallback(uri: Uri?) {
    if (callback != null) {
      callback?.handleRegistrationCallback(uri)
      callback = null
    }
  }

  /**
   * Cancel registration action in case of web browser error
   */
  fun onRegistrationCanceled() {
    if (callback != null) {
      callback?.denyRegistration()
      callback = null
    }
  }

  override fun startRegistration(uri: Uri, oneginiBrowserRegistrationCallback: OneginiBrowserRegistrationCallback) {
    callback = oneginiBrowserRegistrationCallback
    eventEmitter.onSendUrl(uri);
  }
}