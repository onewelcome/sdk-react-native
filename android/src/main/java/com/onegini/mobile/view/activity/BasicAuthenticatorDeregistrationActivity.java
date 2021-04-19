package com.onegini.mobile.view.activity;

import com.onegini.mobile.R;
import com.onegini.mobile.view.actions.basicauth.BasicCustomAuthDeregistrationAction;

public class BasicAuthenticatorDeregistrationActivity extends BasicAuthenticatorActivity {

  @Override
  protected void setTitle() {
    titleText.setText(R.string.custom_auth_deregistration_title);
  }

  @Override
  protected void onSuccess() {
    if (BasicCustomAuthDeregistrationAction.CALLBACK != null) {
      BasicCustomAuthDeregistrationAction.CALLBACK.acceptDeregistrationRequest("");
    }
  }

  @Override
  protected void onFailure() {
    if (BasicCustomAuthDeregistrationAction.CALLBACK != null) {
      BasicCustomAuthDeregistrationAction.CALLBACK.denyDeregistrationRequest();
    }
  }

  @Override
  protected void onError() {
    if (BasicCustomAuthDeregistrationAction.CALLBACK != null) {
      BasicCustomAuthDeregistrationAction.CALLBACK.returnError(new Exception("Fake exception"));
    }
  }
}
