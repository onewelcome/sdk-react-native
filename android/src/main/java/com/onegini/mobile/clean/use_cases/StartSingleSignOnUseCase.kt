package com.onegini.mobile.clean.use_cases

import android.net.Uri
import com.facebook.react.bridge.Promise
import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.mapers.OneginiAppToWebSingleSignOnMapper
import com.onegini.mobile.sdk.android.handlers.OneginiAppToWebSingleSignOnHandler
import com.onegini.mobile.sdk.android.handlers.error.OneginiAppToWebSingleSignOnError
import com.onegini.mobile.sdk.android.model.OneginiAppToWebSingleSignOn

class StartSingleSignOnUseCase(private val oneginiSDK: OneginiSDK) {

    operator fun invoke(url: String, promise: Promise) {
        val targetUri = Uri.parse(url)

        oneginiSDK.oneginiClient.userClient.getAppToWebSingleSignOn(
            targetUri,
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
