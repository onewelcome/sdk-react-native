package com.onegini.mobile.sdk.reactnative

import android.util.Log
import com.facebook.react.modules.core.DeviceEventManagerModule
import javax.inject.Inject

class ExampleClassToBeInjected @Inject constructor(private val eventEmitter: DeviceEventManagerModule.RCTDeviceEventEmitter) {

  fun doSomething(){
    Log.d("ExampleClassToBeInjected", "I'm doing something + ${this.hashCode()}")
    eventEmitter.emit("example_event", "test")
  }
}