package com.onegini.mobile.view.activity;

import com.onegini.mobile.R;
import com.onegini.mobile.model.BasicCustomAuthenticator;
import com.onegini.mobile.view.actions.basicauth.BasicCustomAuthAuthenticationAction;

public class BasicAuthenticatorAuthenticationActivity extends BasicAuthenticatorActivity {

  @Override
  protected void setTitle() {
    titleText.setText(R.string.custom_auth_authentication_title);
  }

  @Override
  protected void onSuccess() {
    if (BasicCustomAuthAuthenticationAction.CALLBACK != null) {
      BasicCustomAuthAuthenticationAction.CALLBACK.returnSuccess(BasicCustomAuthenticator.AUTH_DATA);
    }
  }

  @Override
  protected void onFailure() {
    if (BasicCustomAuthAuthenticationAction.CALLBACK != null) {
      BasicCustomAuthAuthenticationAction.CALLBACK.returnError(new Exception("Authentication failed"));
    }
  }

  @Override
  protected void onError() {
    if (BasicCustomAuthAuthenticationAction.CALLBACK != null) {
      BasicCustomAuthAuthenticationAction.CALLBACK.returnError(new Exception("Fake exception"));
    }
  }
}
