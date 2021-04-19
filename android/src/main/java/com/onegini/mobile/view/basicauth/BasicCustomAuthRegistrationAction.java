package com.onegini.mobile.view.actions.basicauth;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Context;
import android.content.Intent;

import com.onegini.mobile.sdk.android.handlers.action.OneginiCustomAuthRegistrationAction;
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiCustomAuthRegistrationCallback;
import com.onegini.mobile.view.activity.BasicAuthenticatorRegistrationActivity;

public class BasicCustomAuthRegistrationAction implements OneginiCustomAuthRegistrationAction {

  public static OneginiCustomAuthRegistrationCallback CALLBACK;

  private final Context context;

  public BasicCustomAuthRegistrationAction(final Context context) {
    this.context = context;
  }

  @Override
  public void finishRegistration(final OneginiCustomAuthRegistrationCallback callback) {
    CALLBACK = callback;

    final Intent intent = new Intent(context, BasicAuthenticatorRegistrationActivity.class);
    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }
}
