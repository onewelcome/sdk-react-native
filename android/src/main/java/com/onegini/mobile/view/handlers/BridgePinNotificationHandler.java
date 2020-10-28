package com.onegini.mobile.view.handlers;

import javax.annotation.Nullable;

public interface BridgePinNotificationHandler {

  void onNotify(String type, @Nullable Boolean isCreatePinFlow);

  void onError(String message);
}
