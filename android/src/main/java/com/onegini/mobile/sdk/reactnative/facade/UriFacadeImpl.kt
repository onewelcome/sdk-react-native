package com.onegini.mobile.sdk.reactnative.facade

import javax.inject.Singleton
import javax.inject.Inject
import com.onegini.mobile.sdk.reactnative.facade.UriFacade
import android.net.Uri

@Singleton
class UriFacadeImpl @Inject constructor() : UriFacade {
    override fun parse(string: String): Uri {
        return Uri.parse(string)
    }

    override fun withAppendedPath(baseUri: Uri, pathSegment: String): Uri {
        return Uri.withAppendedPath(baseUri, pathSegment)
    }
}