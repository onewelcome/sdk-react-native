package com.onegini.mobile.view.activity;

import android.os.Bundle;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.onegini.mobile.R;
import com.onegini.mobile.view.handlers.BasicCustomAuthenticationRequestHandler;

public class CustomAuthActivity extends AuthenticationActivity {

  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_custom);
    ButterKnife.bind(this);
    initialize();
  }

  @Override
  protected void initialize() {
    parseIntent();
    updateTexts();
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.auth_accept_button)
  public void onAcceptClicked() {
    if (BasicCustomAuthenticationRequestHandler.CALLBACK != null) {
      BasicCustomAuthenticationRequestHandler.CALLBACK.acceptAuthenticationRequest();
    }
  }

  @SuppressWarnings("unused")
  @OnClick(R.id.auth_deny_button)
  public void onDenyClicked() {
    if (BasicCustomAuthenticationRequestHandler.CALLBACK != null) {
      BasicCustomAuthenticationRequestHandler.CALLBACK.denyAuthenticationRequest();
    }
  }


  @SuppressWarnings("unused")
  @OnClick(R.id.fallback_to_pin_button)
  public void onFallbackClicked() {
    if (BasicCustomAuthenticationRequestHandler.CALLBACK != null) {
      BasicCustomAuthenticationRequestHandler.CALLBACK.fallbackToPin();
    }
  }
}
