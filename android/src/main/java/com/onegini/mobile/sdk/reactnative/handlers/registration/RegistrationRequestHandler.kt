package com.onegini.mobile.sdk.reactnative.handlers.registration

import com.onegini.mobile.sdk.android.handlers.request.OneginiBrowserRegistrationRequestHandler
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiBrowserRegistrationCallback
import android.net.Uri
import com.onegini.mobile.sdk.reactnative.exception.OneginiReactNativeException
import com.onegini.mobile.sdk.reactnative.exception.OneginiWrapperErrors.REGISTRATION_NOT_IN_PROGRESS
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegistrationRequestHandler @Inject constructor(private val eventEmitter: RegistrationEventEmitter):
    OneginiBrowserRegistrationRequestHandler {
  
  private var callback: OneginiBrowserRegistrationCallback? = null
  /**
   * Finish registration action with result from web browser
   */
  @Throws(OneginiReactNativeException::class)

  fun handleRegistrationCallback(uri: Uri?) {
    callback?.let { registrationCallback ->
      registrationCallback.handleRegistrationCallback(uri)
      callback = null
    }  ?: throw OneginiReactNativeException(REGISTRATION_NOT_IN_PROGRESS.code.toInt(), REGISTRATION_NOT_IN_PROGRESS.message)
  }

  /**
   * Cancel registration action in case of web browser error
   */
  @Throws(OneginiReactNativeException::class)
  fun cancelRegistration() {
    callback?.let { registrationCallback ->
      registrationCallback.denyRegistration()
      callback = null
    } ?: throw OneginiReactNativeException(REGISTRATION_NOT_IN_PROGRESS.code.toInt(), REGISTRATION_NOT_IN_PROGRESS.message)
  }

  override fun startRegistration(uri: Uri, oneginiBrowserRegistrationCallback: OneginiBrowserRegistrationCallback) {
    callback = oneginiBrowserRegistrationCallback
    eventEmitter.onSendUrl(uri);
  }
}
