package com.onegini.mobile.view.actions.basicauth;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Context;
import android.content.Intent;

import com.onegini.mobile.sdk.android.handlers.action.OneginiCustomAuthDeregistrationAction;
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiCustomAuthDeregistrationCallback;
import com.onegini.mobile.view.activity.BasicAuthenticatorDeregistrationActivity;

public class BasicCustomAuthDeregistrationAction implements OneginiCustomAuthDeregistrationAction {

  public static OneginiCustomAuthDeregistrationCallback CALLBACK;

  private final Context context;

  public BasicCustomAuthDeregistrationAction(final Context context) {
    this.context = context;
  }

  @Override
  public void finishDeregistration(final OneginiCustomAuthDeregistrationCallback callback, final String s) {
    CALLBACK = callback;

    final Intent intent = new Intent(context, BasicAuthenticatorDeregistrationActivity.class);
    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }
}
