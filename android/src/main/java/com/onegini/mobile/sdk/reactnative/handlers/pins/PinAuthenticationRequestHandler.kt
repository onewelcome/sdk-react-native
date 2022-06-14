package com.onegini.mobile.sdk.reactnative.handlers.pins

import com.onegini.mobile.sdk.android.handlers.request.OneginiPinAuthenticationRequestHandler
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiPinCallback
import com.onegini.mobile.sdk.android.model.entity.AuthenticationAttemptCounter
import com.onegini.mobile.sdk.android.model.entity.UserProfile
import com.onegini.mobile.sdk.reactnative.Constants
import com.onegini.mobile.sdk.reactnative.OneginiSDK

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
        pinNotificationObserver?.onNotify(Constants.PIN_NOTIFICATION_OPEN_VIEW, Constants.PinFlow.Authentication, userProfileId, null)
    }

    override fun onNextAuthenticationAttempt(attemptCounter: AuthenticationAttemptCounter) {
        pinNotificationObserver?.onWrongPin(attemptCounter.remainingAttempts)
    }

    override fun finishAuthentication() {
        pinNotificationObserver?.onNotify(Constants.PIN_NOTIFICATION_CLOSE_VIEW, Constants.PinFlow.Authentication, null, null)
    }

    fun acceptAuthenticationRequest(var1: CharArray?) {
        callback?.acceptAuthenticationRequest(var1)
    }

    fun denyAuthenticationRequest() {
        callback?.denyAuthenticationRequest()
    }
}
