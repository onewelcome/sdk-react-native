package com.onegini.mobile.sdk.reactnative.di

import com.onegini.mobile.sdk.reactnative.RNOneginiSdk
import dagger.Component
import javax.inject.Singleton

@Component(modules = [LibraryModule::class])
@Singleton
interface LibraryComponent {

  fun inject(rnOneginiSdk: RNOneginiSdk)
}