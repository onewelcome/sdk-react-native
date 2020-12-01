package com.onegini.mobile.view.handlers.customregistration

import com.onegini.mobile.sdk.android.model.entity.CustomInfo

interface CustomRegistrationObserver {

    fun initRegistration(idProvider: String, info: CustomInfo?)

    fun finishRegistration(idProvider: String, info: CustomInfo?)
}