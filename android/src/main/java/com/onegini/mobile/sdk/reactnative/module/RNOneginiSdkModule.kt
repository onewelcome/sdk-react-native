package com.onegini.mobile.sdk.reactnative.module

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.modules.core.DeviceEventManagerModule
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RNOneginiSdkModule(private val reactApplicationContext: ReactApplicationContext) {

  @Provides
  @Singleton
  fun provideContext() = reactApplicationContext.applicationContext

  @Provides
  @Singleton
  fun provideReactApplicationContext() = reactApplicationContext

  @Provides
  @Singleton
  fun provideDeviceEventEmitter(): DeviceEventManagerModule.RCTDeviceEventEmitter {
    return reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
  }
}
