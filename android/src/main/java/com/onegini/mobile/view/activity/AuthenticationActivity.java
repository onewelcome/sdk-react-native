package com.onegini.mobile.view.activity;

import static com.onegini.mobile.Constants.COMMAND_FINISH;
import static com.onegini.mobile.Constants.COMMAND_START;
import static com.onegini.mobile.Constants.EXTRA_COMMAND;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import butterknife.BindView;
import com.onegini.mobile.R;
import com.onegini.mobile.storage.UserStorage;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;

public abstract class AuthenticationActivity extends Activity {

  public static final String EXTRA_MESSAGE = "message";
  public static final String EXTRA_ERROR_MESSAGE = "error_message";
  public static final String EXTRA_USER_PROFILE_ID = "user_profile_id";

  @SuppressWarnings({ "unused", "WeakerAccess" })
  @BindView(R.id.welcome_user_text)
  TextView welcomeTextView;
  @SuppressWarnings({ "unused", "WeakerAccess" })
  @BindView(R.id.authenticator_message)
  TextView authenticatorMessage;
  @SuppressWarnings({ "unused" })
  @BindView(R.id.auth_cancel_button)
  Button cancelButton;

  protected String errorMessage;
  protected String command;
  private String message;
  private String userName;

  protected abstract void initialize();

  @Override
  protected void onNewIntent(final Intent intent) {
    setIntent(intent);
    initialize();
  }

  protected void parseIntent() {
    final Bundle extras = getIntent().getExtras();
    command = extras.getString(EXTRA_COMMAND);
    if (COMMAND_FINISH.equals(command)) {
      finish();
    } else if (COMMAND_START.equals(command)) {
      message = extras.getString(EXTRA_MESSAGE, "");
      errorMessage = extras.getString(EXTRA_ERROR_MESSAGE, "");

      final String userProfileId = extras.getString(EXTRA_USER_PROFILE_ID, "");
      loadUserName(userProfileId);
    }
  }

  private void loadUserName(final String userProfileId) {
    if (TextUtils.isEmpty(userProfileId)) {
      return;
    }
    final UserStorage userStorage = new UserStorage(this);
    userName = userStorage.loadUser(new UserProfile(userProfileId)).getName();
  }

  protected void updateTexts() {
    updateWelcomeText();
    updateAuthenticatorMessage();
  }

  private void updateWelcomeText() {
    if (isNotBlank(userName)) {
      welcomeTextView.setText(getString(R.string.welcome_user_text, userName));
    } else {
      welcomeTextView.setVisibility(View.INVISIBLE);
    }
  }

  private void updateAuthenticatorMessage() {
    if (isNotBlank(message)) {
      authenticatorMessage.setText(message);
    } else if (isBlank(authenticatorMessage.getText().toString())) {
      authenticatorMessage.setVisibility(View.INVISIBLE);
    }
  }

  protected boolean isNotBlank(final String string) {
    return !isBlank(string);
  }

  private boolean isBlank(final String string) {
    return string == null || string.isEmpty();
  }

  @Override
  public void onBackPressed() {
    if (isCancellable()) {
      cancelRequest();
      super.onBackPressed();
    }
  }

  protected boolean isCancellable() {
    //by default we don't want to be able to go back from the pin screen,
    //but if cancel button is visible, we want to be able to use back button as well
    return isCancelButtonVisible();
  }

  private boolean isCancelButtonVisible() {
    if (cancelButton == null || cancelButton.getVisibility() == View.GONE) {
      return false;
    }
    return true;
  }

  protected void cancelRequest() {
  }
}
