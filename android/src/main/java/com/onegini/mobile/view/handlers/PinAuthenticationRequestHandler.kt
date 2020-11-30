package com.onegini.mobile.view.handlers

import com.onegini.mobile.Constants
import com.onegini.mobile.OneginiSDK
import com.onegini.mobile.mapers.AuthenticationAttemptCounterMapper
import com.onegini.mobile.sdk.android.handlers.request.OneginiPinAuthenticationRequestHandler
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiPinCallback
import com.onegini.mobile.sdk.android.model.entity.AuthenticationAttemptCounter
import com.onegini.mobile.sdk.android.model.entity.UserProfile

class PinAuthenticationRequestHandler(private val oneginiSDK: OneginiSDK) : OneginiPinAuthenticationRequestHandler, RegisteredHandler {
    private var callback: OneginiPinCallback? = null
    private var userProfileId: String? = null
    private var pinNotificationObserver: PinNotificationObserver? = null
    fun setPinNotificationObserver(pinNotificationObserver: PinNotificationObserver?) {
        this.pinNotificationObserver = pinNotificationObserver
    }

    override fun startAuthentication(userProfile: UserProfile, oneginiPinCallback: OneginiPinCallback,
                                     attemptCounter: AuthenticationAttemptCounter) {
        userProfileId = userProfile.profileId //@todo Might need this in the future
        callback = oneginiPinCallback
        if (pinNotificationObserver != null) {
            pinNotificationObserver!!.onNotify(Constants.PIN_NOTIFICATION_OPEN_VIEW, Constants.PinFlow.Authentication)
        }
    }

    override fun onNextAuthenticationAttempt(attemptCounter: AuthenticationAttemptCounter) {
        if (pinNotificationObserver != null) {
            pinNotificationObserver!!.onError(AuthenticationAttemptCounterMapper.toErrorString(attemptCounter))
        }
    }

    override fun finishAuthentication() {
        if (pinNotificationObserver != null) {
            pinNotificationObserver!!.onNotify(Constants.PIN_NOTIFICATION_CLOSE_VIEW, Constants.PinFlow.Authentication)
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

    override fun onStart() {

    }
}