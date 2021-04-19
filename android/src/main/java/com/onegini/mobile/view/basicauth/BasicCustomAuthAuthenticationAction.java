package com.onegini.mobile.view.actions.basicauth;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Context;
import android.content.Intent;

import com.onegini.mobile.sdk.android.handlers.action.OneginiCustomAuthAuthenticationAction;
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiCustomAuthAuthenticationCallback;
import com.onegini.mobile.view.activity.BasicAuthenticatorAuthenticationActivity;

public class BasicCustomAuthAuthenticationAction implements OneginiCustomAuthAuthenticationAction {

  public static OneginiCustomAuthAuthenticationCallback CALLBACK;

  private final Context context;

  public BasicCustomAuthAuthenticationAction(final Context context) {
    this.context = context;
  }

  @Override
  public void finishAuthentication(final OneginiCustomAuthAuthenticationCallback callback, final String s) {
    CALLBACK = callback;

    final Intent intent = new Intent(context, BasicAuthenticatorAuthenticationActivity.class);
    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }
}
