package com.onegini.mobile.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.onegini.mobile.OneginiSDK;
import com.onegini.mobile.view.handlers.RegistrationRequestHandler;
import com.onegini.mobile.sdk.android.client.OneginiClient;

public class MineRegistrationActivity extends Activity {


  /*<activity
  android:name="com.onegini.mobile.view.activity.MineRegistrationActivity"
  android:launchMode="singleTask"
  android:screenOrientation="portrait">
            <intent-filter>
                <action android:name="android.intent.action.VIEW"/>

                <category android:name="android.intent.category.DEFAULT"/>
                <category android:name="android.intent.category.BROWSABLE"/>

                <data android:scheme="reactnativeexample"/>
            </intent-filter>
        </activity>*/

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onNewIntent(final Intent intent) {
    super.onNewIntent(intent);
    handleRedirection(intent.getData());
  }

  private void handleRedirection(final Uri uri) {
    if (uri == null) {
      return;
    }

    final OneginiClient client = OneginiSDK.getOneginiClient(getApplicationContext());
    final String redirectUri = client.getConfigModel().getRedirectUri();
    if (redirectUri.startsWith(uri.getScheme())) {
      RegistrationRequestHandler.handleRegistrationCallback(uri);
    }
  }
}
