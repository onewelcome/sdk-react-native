package com.onegini.mobile.view.handlers;

import static com.onegini.mobile.Constants.PIN_NOTIFICATION_CLOSE_VIEW;
import static com.onegini.mobile.Constants.PIN_NOTIFICATION_OPEN_VIEW;

import android.content.Context;

import com.onegini.mobile.RNOneginiSdkModule;
import com.onegini.mobile.sdk.android.handlers.request.OneginiPinAuthenticationRequestHandler;
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiPinCallback;
import com.onegini.mobile.sdk.android.model.entity.AuthenticationAttemptCounter;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;

public class PinAuthenticationRequestHandler implements OneginiPinAuthenticationRequestHandler {

  public static OneginiPinCallback CALLBACK;

  private final Context context;
  private String userProfileId;

  public PinAuthenticationRequestHandler(final Context context) {
    this.context = context;
  }

  @Override
  public void startAuthentication(final UserProfile userProfile, final OneginiPinCallback oneginiPinCallback,
                                  final AuthenticationAttemptCounter attemptCounter) {
    userProfileId = userProfile.getProfileId(); //@todo Might need this in the future
    CALLBACK = oneginiPinCallback;

    RNOneginiSdkModule.pinNotificationHandler.onNotify(PIN_NOTIFICATION_OPEN_VIEW, false);
  }

  @Override
  public void onNextAuthenticationAttempt(final AuthenticationAttemptCounter attemptCounter) {
    RNOneginiSdkModule.pinNotificationHandler.onNotify(PIN_NOTIFICATION_OPEN_VIEW, null);
  }

  @Override
  public void finishAuthentication() {
    RNOneginiSdkModule.pinNotificationHandler.onNotify(PIN_NOTIFICATION_CLOSE_VIEW, null);
  }
}
