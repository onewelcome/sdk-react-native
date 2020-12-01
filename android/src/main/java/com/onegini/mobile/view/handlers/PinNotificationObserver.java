//@todo Later will be transferred to RN Wrapper
package com.onegini.mobile.view.handlers;

import androidx.annotation.NonNull;

import com.onegini.mobile.Constants;

import javax.annotation.Nullable;

public interface PinNotificationObserver {

  void onNotify(String type, @NonNull Constants.PinFlow flow);

  void onError(String message);
}
