package com.onegini.mobile.sdk.reactnative.model

import com.onegini.mobile.sdk.reactnative.network.ApiCall
import okhttp3.Headers

data class ResourceRequestDetails(
        val path: String,
        val method: ApiCall,
        val headers: Headers,
        val body: String?
)
