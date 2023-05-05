package com.onegini.mobile.sdk.reactnative.facade

import android.net.Uri
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UriFacadeImpl @Inject constructor() : UriFacade {
  override fun parse(string: String): Uri {
    return Uri.parse(string)
  }

  override fun withAppendedPath(baseUri: Uri, pathSegment: String): Uri {
    return Uri.withAppendedPath(baseUri, pathSegment)
  }
}