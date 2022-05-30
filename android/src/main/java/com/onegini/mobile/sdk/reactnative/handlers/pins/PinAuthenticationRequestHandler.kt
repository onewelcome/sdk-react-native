package com.onegini.mobile.sdk.reactnative.handlers.pins

import com.onegini.mobile.sdk.reactnative.Constants
import com.onegini.mobile.sdk.reactnative.OneginiSDK
import com.onegini.mobile.sdk.android.handlers.request.OneginiPinAuthenticationRequestHandler
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiPinCallback
import com.onegini.mobile.sdk.android.model.entity.AuthenticationAttemptCounter
import com.onegini.mobile.sdk.android.model.entity.UserProfile

class PinAuthenticationRequestHandler(private val oneginiSDK: OneginiSDK) : OneginiPinAuthenticationRequestHandler {
    private var callback: OneginiPinCallback? = null
    private var userProfileId: String? = null
    private var pinNotificationObserver: PinNotificationObserver? = null
    fun setPinNotificationObserver(pinNotificationObserver: PinNotificationObserver?) {
        this.pinNotificationObserver = pinNotificationObserver
    }

    override fun startAuthentication(
        userProfile: UserProfile,
        oneginiPinCallback: OneginiPinCallback,
        attemptCounter: AuthenticationAttemptCounter
    ) {
        userProfileId = userProfile.profileId // @todo Might need this in the future
        callback = oneginiPinCallback
        if (pinNotificationObserver != null) {
            pinNotificationObserver!!.onNotify(Constants.PIN_NOTIFICATION_OPEN_VIEW, Constants.PinFlow.Authentication, userProfileId, null)
        }
    }

    override fun onNextAuthenticationAttempt(attemptCounter: AuthenticationAttemptCounter) {
        if (pinNotificationObserver != null) {
            pinNotificationObserver!!.onWrongPin(attemptCounter.remainingAttempts)
        }
    }

    override fun finishAuthentication() {
        if (pinNotificationObserver != null) {
            pinNotificationObserver!!.onNotify(Constants.PIN_NOTIFICATION_CLOSE_VIEW, Constants.PinFlow.Authentication, null, null)
        }
    }

    fun acceptAuthenticationRequest(var1: CharArray?) {
        if (callback != null) {
            callback!!.acceptAuthenticationRequest(var1)
        }
    }

    fun denyAuthenticationRequest() {
        if (callback != null) {
            callback!!.denyAuthenticationRequest()
        }
    }
}
