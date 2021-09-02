package com.onegini.mobile.sdk.reactnative.model

import com.onegini.mobile.sdk.reactnative.network.ApiCall

data class ResourceRequestDetails(
        val path: String,
        val method: ApiCall,
        val encoding: String,
        val headers: Map<String, String>,
        val parameters: Map<String, String>
)
