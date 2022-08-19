package com.onegini.mobile.sdk.reactnative.exception

import com.onegini.mobile.sdk.android.handlers.error.OneginiErrorDetails
import com.onegini.mobile.sdk.android.model.OneginiAuthenticator
import com.onegini.mobile.sdk.android.model.entity.CustomInfo
import com.onegini.mobile.sdk.android.model.entity.UserProfile

class EmptyOneginiErrorDetails : OneginiErrorDetails {
    override fun getCustomInfo(): CustomInfo? {
        return null
    }

    override fun getUserProfile(): UserProfile? {
        return null
    }

    override fun getAuthenticator(): OneginiAuthenticator? {
        return null
    }
}
