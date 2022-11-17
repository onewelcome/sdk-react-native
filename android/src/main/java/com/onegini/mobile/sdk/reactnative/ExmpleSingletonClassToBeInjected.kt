package com.onegini.mobile.sdk.reactnative

import android.util.Log
import com.facebook.react.modules.core.DeviceEventManagerModule
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExampleSingletonClassToBeInjected @Inject constructor(private val eventEmitter: DeviceEventManagerModule.RCTDeviceEventEmitter) {

  fun doSomething(){
    Log.d("ExampleSingletonClassToBeInjected", "I'm doing something + ${this.hashCode()}")
    eventEmitter.emit("example_event", "test")
  }
}