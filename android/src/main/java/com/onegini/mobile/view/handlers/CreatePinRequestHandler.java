package com.onegini.mobile.view.handlers;

import static com.onegini.mobile.Constants.PIN_NOTIFICATION_CLOSE_VIEW;
import static com.onegini.mobile.Constants.PIN_NOTIFICATION_CONFIRM_VIEW;
import static com.onegini.mobile.Constants.PIN_NOTIFICATION_OPEN_VIEW;

import java.util.Arrays;

import android.content.Context;

import com.onegini.mobile.RNOneginiSdkModule;
import com.onegini.mobile.OneginiSDK;
import com.onegini.mobile.util.DeregistrationUtil;
import com.onegini.mobile.sdk.android.handlers.OneginiPinValidationHandler;
import com.onegini.mobile.sdk.android.handlers.error.OneginiPinValidationError;
import com.onegini.mobile.sdk.android.handlers.request.OneginiCreatePinRequestHandler;
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiPinCallback;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;
import com.onegini.mobile.R;

public class CreatePinRequestHandler implements OneginiCreatePinRequestHandler {

  public static PinWithConfirmationHandler CALLBACK;

  private final Context context;

  public CreatePinRequestHandler(final Context context) {
    this.context = context;
  }

  @Override
  public void startPinCreation(final UserProfile userProfile, final OneginiPinCallback oneginiPinCallback, final int pinLength) {
    notifyOnOpen();

    CALLBACK = new PinWithConfirmationHandler(oneginiPinCallback);
  }

  @Override
  public void onNextPinCreationAttempt(final OneginiPinValidationError oneginiPinValidationError) {
    handlePinValidationError(oneginiPinValidationError);
  }

  @Override
  public void finishPinCreation() {
    notifyOnSimpleAction(PIN_NOTIFICATION_CLOSE_VIEW);
  }

  /**
   * Extended pin handler, used to create PIN verification step
   */
  public class PinWithConfirmationHandler {

    private final OneginiPinCallback originalHandler;

    private char[] pin;

    public PinWithConfirmationHandler(final OneginiPinCallback originalHandler) {
      this.originalHandler = originalHandler;
    }

    public void onPinProvided(final char[] pin) {
      if (isPinSet()) {
        secondPinProvided(pin);
      } else {
        firstPinProvided(pin);
      }
    }

    private void firstPinProvided(final char[] pin) {
      OneginiSDK.getOneginiClient(context).getUserClient().validatePinWithPolicy(pin, new OneginiPinValidationHandler() {
        @Override
        public void onSuccess() {
          PinWithConfirmationHandler.this.pin = pin;
          notifyOnSimpleAction(PIN_NOTIFICATION_CONFIRM_VIEW);
        }

        @Override
        public void onError(final OneginiPinValidationError oneginiPinValidationError) {
          handlePinValidationError(oneginiPinValidationError);
        }
      });
    }

    public void secondPinProvided(final char[] pin) {
      final boolean pinsEqual = Arrays.equals(this.pin, pin);
      nullifyPinArray();
      if (pinsEqual) {
        originalHandler.acceptAuthenticationRequest(pin);
      } else {
        notifyOnError(context.getString(R.string.pin_error_not_equal));
      }
    }

    public void pinCancelled(){
      nullifyPinArray();
      originalHandler.denyAuthenticationRequest();
    }

    private boolean isPinSet() {
      return pin != null;
    }

    private void nullifyPinArray() {
      if (isPinSet()) {
        final int arraySize = pin.length;
        for (int i = 0; i < arraySize; i++) {
          pin[i] = '\0';
        }
        pin = null;
      }
    }
  }

  private void handlePinValidationError(final OneginiPinValidationError oneginiPinValidationError) {
    @OneginiPinValidationError.PinValidationErrorType int errorType = oneginiPinValidationError.getErrorType();
    switch (errorType) {
      case OneginiPinValidationError.WRONG_PIN_LENGTH:
        notifyOnError(context.getString(R.string.pin_error_invalid_length));
        break;
      case OneginiPinValidationError.PIN_BLACKLISTED:
        notifyOnError(context.getString(R.string.pin_error_blacklisted));
        break;
      case OneginiPinValidationError.PIN_IS_A_SEQUENCE:
        notifyOnError(context.getString(R.string.pin_error_sequence));
        break;
      case OneginiPinValidationError.PIN_USES_SIMILAR_DIGITS:
        notifyOnError(context.getString(R.string.pin_error_similar));
        break;
      case OneginiPinValidationError.DEVICE_DEREGISTERED:
        new DeregistrationUtil(context).onDeviceDeregistered();
        //startLoginActivity(parseErrorMessage(oneginiPinValidationError));
        break;
      case OneginiPinValidationError.GENERAL_ERROR:
      default:
        notifyOnError(oneginiPinValidationError.getMessage());
        break;
    }
  }

  private void notifyOnSimpleAction(final String notifyAction) {
    RNOneginiSdkModule.pinNotificationHandler.onNotify(notifyAction, null);
  }

  private void notifyOnOpen() {
    RNOneginiSdkModule.pinNotificationHandler.onNotify(PIN_NOTIFICATION_OPEN_VIEW, true);
  }

  private void notifyOnError(final String errorMessage) {
    RNOneginiSdkModule.pinNotificationHandler.onError(errorMessage);
  }

/*  private void startLoginActivity(final String errorMessage) {
    final Intent intent = new Intent(context, LoginActivity.class);
    intent.putExtra(LoginActivity.ERROR_MESSAGE_EXTRA, errorMessage);
    intent.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
    context.startActivity(intent);
  }*/

}
