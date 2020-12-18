package com.onegini.mobile.view.handlers.pins

import com.onegini.mobile.Constants.PinFlow
import com.onegini.mobile.sdk.android.handlers.error.OneginiError


interface PinNotificationObserver {
    fun onNotify(event: String, flow: PinFlow)
    fun onError(error: OneginiError?, flow: PinFlow)
}