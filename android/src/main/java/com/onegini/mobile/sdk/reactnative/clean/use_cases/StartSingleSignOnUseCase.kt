package com.onegini.mobile.sdk.reactnative.clean.use_cases

import com.facebook.react.bridge.Promise
import com.onegini.mobile.sdk.android.handlers.OneginiAppToWebSingleSignOnHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiAppToWebSingleSignOnError
import com.onegini.mobile.sdk.android.model.OneginiAppToWebSingleSignOn
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.reactnative.facade.UriFacade
import com.onegini.mobile.sdk.reactnative.mapers.OneginiAppToWebSingleSignOnMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StartSingleSignOnUseCase @Inject constructor(private val oneginiSDK: OneginiSDK, private val uriFacade: UriFacade) {

    operator fun invoke (uri: String, promise: Promise) {
        oneginiSDK.oneginiClient.userClient.getAppToWebSingleSignOn(
            uriFacade.parse(uri),
            object : OneginiAppToWebSingleSignOnHandler {
                override fun onSuccess(oneginiAppToWebSingleSignOn: OneginiAppToWebSingleSignOn) {
                    promise.resolve(OneginiAppToWebSingleSignOnMapper.toWritableMap(oneginiAppToWebSingleSignOn))
                }

                override fun onError(error: OneginiAppToWebSingleSignOnError) {
                    promise.reject(error.errorType.toString(), error.message)
                }
            }
        )
    }
}
