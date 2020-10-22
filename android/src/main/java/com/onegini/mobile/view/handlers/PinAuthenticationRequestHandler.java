package com.onegini.mobile.view.handlers;

import static com.onegini.mobile.Constants.COMMAND_FINISH;
import static com.onegini.mobile.Constants.COMMAND_START;
import static com.onegini.mobile.Constants.EXTRA_COMMAND;
import static com.onegini.mobile.view.activity.AuthenticationActivity.EXTRA_MESSAGE;
import static com.onegini.mobile.view.activity.AuthenticationActivity.EXTRA_USER_PROFILE_ID;
import static com.onegini.mobile.view.activity.PinActivity.EXTRA_FAILED_ATTEMPTS_COUNT;
import static com.onegini.mobile.view.activity.PinActivity.EXTRA_MAX_FAILED_ATTEMPTS;

import android.content.Context;
import android.content.Intent;
import com.onegini.mobile.R;
import com.onegini.mobile.view.activity.PinActivity;
import com.onegini.mobile.sdk.android.handlers.request.OneginiPinAuthenticationRequestHandler;
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiPinCallback;
import com.onegini.mobile.sdk.android.model.entity.AuthenticationAttemptCounter;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;

public class PinAuthenticationRequestHandler implements OneginiPinAuthenticationRequestHandler {

  public static OneginiPinCallback CALLBACK;

  private final Context context;
  private String userProfileId;
  private int failedAttemptsCount;
  private int maxAttemptsCount;

  public PinAuthenticationRequestHandler(final Context context) {
    this.context = context;
  }

  @Override
  public void startAuthentication(final UserProfile userProfile, final OneginiPinCallback oneginiPinCallback,
                                  final AuthenticationAttemptCounter attemptCounter) {
    userProfileId = userProfile.getProfileId();
    CALLBACK = oneginiPinCallback;
    failedAttemptsCount = maxAttemptsCount = 0;

    PinActivity.setIsCreatePinFlow(false);
    notifyActivity();
  }

  @Override
  public void onNextAuthenticationAttempt(final AuthenticationAttemptCounter attemptCounter) {
    failedAttemptsCount = attemptCounter.getFailedAttempts();
    maxAttemptsCount = attemptCounter.getMaxAttempts();
    notifyActivity();
  }

  @Override
  public void finishAuthentication() {
    notifyActivity(COMMAND_FINISH);
  }

  private void notifyActivity() {
    notifyActivity(COMMAND_START);
  }

  private void notifyActivity(final String command) {
    final Intent intent = new Intent(context, PinActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    intent.putExtra(EXTRA_MESSAGE, context.getString(R.string.authenticator_message_enter_pin));
    intent.putExtra(EXTRA_USER_PROFILE_ID, userProfileId);
    intent.putExtra(EXTRA_FAILED_ATTEMPTS_COUNT, failedAttemptsCount);
    intent.putExtra(EXTRA_MAX_FAILED_ATTEMPTS, maxAttemptsCount);
    intent.putExtra(EXTRA_COMMAND, command);
    context.startActivity(intent);
  }
}
