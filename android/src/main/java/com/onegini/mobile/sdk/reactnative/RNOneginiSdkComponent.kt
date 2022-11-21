package com.onegini.mobile.sdk.reactnative

import com.onegini.mobile.sdk.reactnative.module.RNOneginiSdkModule
import dagger.Component
import javax.inject.Singleton

@Component(modules = [RNOneginiSdkModule::class])
@Singleton
interface RNOneginiSdkComponent {

  fun inject(rnOneginiSdk: RNOneginiSdk)
}