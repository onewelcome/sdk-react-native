package com.onegini.mobile.view.activity;

import com.onegini.mobile.R;
import com.onegini.mobile.model.BasicCustomAuthenticator;
import com.onegini.mobile.view.actions.basicauth.BasicCustomAuthRegistrationAction;

public class BasicAuthenticatorRegistrationActivity extends BasicAuthenticatorActivity {

  @Override
  protected void setTitle() {
    titleText.setText(R.string.custom_auth_registration_title);
  }

  @Override
  protected void onSuccess() {
    if (BasicCustomAuthRegistrationAction.CALLBACK != null) {
      BasicCustomAuthRegistrationAction.CALLBACK.acceptRegistrationRequest(BasicCustomAuthenticator.AUTH_DATA);
    }
  }

  @Override
  protected void onFailure() {
    if (BasicCustomAuthRegistrationAction.CALLBACK != null) {
      BasicCustomAuthRegistrationAction.CALLBACK.denyRegistrationRequest();
    }
  }

  @Override
  protected void onError() {
    if (BasicCustomAuthRegistrationAction.CALLBACK != null) {
      BasicCustomAuthRegistrationAction.CALLBACK.returnError(new Exception("Fake exception"));
    }
  }
}
