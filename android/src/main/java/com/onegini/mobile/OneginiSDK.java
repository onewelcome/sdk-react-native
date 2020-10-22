package com.onegini.mobile;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import android.content.Context;

import com.onegini.mobile.view.handlers.CreatePinRequestHandler;
import com.onegini.mobile.view.handlers.PinAuthenticationRequestHandler;
import com.onegini.mobile.view.handlers.RegistrationRequestHandler;
import com.onegini.mobile.sdk.android.client.OneginiClient;
import com.onegini.mobile.sdk.android.client.OneginiClientBuilder;

public class OneginiSDK {

  public static OneginiClient getOneginiClient(final Context context) {
    OneginiClient oneginiClient = OneginiClient.getInstance();
    if (oneginiClient == null) {
      oneginiClient = buildSDK(context);
    }
    return oneginiClient;
  }

  private static OneginiClient buildSDK(final Context context) {
    final Context applicationContext = context.getApplicationContext();
    final RegistrationRequestHandler registrationRequestHandler = new RegistrationRequestHandler(applicationContext);
    final PinAuthenticationRequestHandler pinAuthenticationRequestHandler = new PinAuthenticationRequestHandler(applicationContext);
    final CreatePinRequestHandler createPinRequestHandler = new CreatePinRequestHandler(applicationContext);

    // will throw OneginiConfigNotFoundException if OneginiConfigModel class can't be found
    return new OneginiClientBuilder(applicationContext, createPinRequestHandler, pinAuthenticationRequestHandler)
        // handlers for optional functionalities
        .setBrowserRegistrationRequestHandler(registrationRequestHandler)
        // Set security controller
        .setSecurityController(SecurityController.class)
        // Set http connect / read timeout
        .setHttpConnectTimeout((int) TimeUnit.SECONDS.toMillis(5))
        .setHttpReadTimeout((int) TimeUnit.SECONDS.toMillis(20))
        .build();
  }
}
