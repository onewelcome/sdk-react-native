package com.onegini.mobile.model

import com.onegini.mobile.network.ApiCall

data class ResourceRequestDetails(
        val path: String,
        val method: ApiCall,
        val encoding: String,
        val headers: Map<String, String>,
        val parameters: Map<String, String>
        )
