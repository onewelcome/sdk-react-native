//@todo Later will be transferred to RN Wrapper
package com.onegini.mobile.view.handlers.pins

import com.onegini.mobile.Constants.PinFlow
import com.onegini.mobile.sdk.android.handlers.error.OneginiChangePinError

interface PinNotificationObserver {
    fun onNotify(event: String, flow: PinFlow)
    fun onError(message: String, flow: PinFlow)
}