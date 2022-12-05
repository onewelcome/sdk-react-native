package com.onegini.mobile.sdk.reactnative.module

import com.onegini.mobile.sdk.reactnative.facade.UriFacade
import com.onegini.mobile.sdk.reactnative.facade.UriFacadeImpl
import dagger.Binds
import dagger.Module

@Module
interface FacadeModule {

    @Binds
    fun bindUriFacade(uriFacade: UriFacadeImpl): UriFacade
}

