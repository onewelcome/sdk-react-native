package com.onegini.mobile.view.handlers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.onegini.mobile.sdk.android.handlers.request.OneginiBrowserRegistrationRequestHandler;
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiBrowserRegistrationCallback;

public class RegistrationRequestHandler implements OneginiBrowserRegistrationRequestHandler {

  private static OneginiBrowserRegistrationCallback CALLBACK;

  /**
   * Finish registration action with result from web browser
   */
  public static void handleRegistrationCallback(final Uri uri) {
    if (CALLBACK != null) {
      CALLBACK.handleRegistrationCallback(uri);
      CALLBACK = null;
    }
  }

  /**
   * Cancel registration action in case of web browser error
   */
  public static void onRegistrationCanceled() {
    if (CALLBACK != null) {
      CALLBACK.denyRegistration();
      CALLBACK = null;
    }
  }

  private final Context context;

  public RegistrationRequestHandler(final Context context) {
    this.context = context;
  }

  @Override
  public void startRegistration(final Uri uri, final OneginiBrowserRegistrationCallback oneginiBrowserRegistrationCallback) {
    CALLBACK = oneginiBrowserRegistrationCallback;

    // We're going to launch external browser to allow user to log in. You could also use embedded WebView instead.
    final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

    context.startActivity(intent);
  }
}
