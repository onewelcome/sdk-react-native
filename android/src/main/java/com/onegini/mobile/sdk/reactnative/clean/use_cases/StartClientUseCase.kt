package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.OneginiInitializationHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiInitializationError
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.exception.rejectOneginiException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StartClientUseCase @Inject constructor(private val oneginiSDK: OneginiSDK) {
  operator fun invoke(promise: Promise) {

    oneginiSDK.oneginiClient.start(object : OneginiInitializationHandler {
      override fun onSuccess(removedUserProfiles: Set<UserProfile>) {
        promise.resolve(null)
        oneginiSDK.setSDKInitialized()
      }

      override fun onError(error: OneginiInitializationError) {
        promise.rejectOneginiException(error)
      }
    })
  }
}
