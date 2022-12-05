package com.onegini.mobile.sdk.reactnative

import com.onegini.mobile.sdk.reactnative.module.FacadeModule
import com.onegini.mobile.sdk.reactnative.module.RNOneginiSdkModule
import com.onegini.mobile.sdk.reactnative.module.SecureResourceClientModule
import dagger.Component
import javax.inject.Singleton

@Component(modules = [RNOneginiSdkModule::class, SecureResourceClientModule::class, FacadeModule::class])
@Singleton
interface RNOneginiSdkComponent {

  fun inject(rnOneginiSdk: RNOneginiSdk)
}
