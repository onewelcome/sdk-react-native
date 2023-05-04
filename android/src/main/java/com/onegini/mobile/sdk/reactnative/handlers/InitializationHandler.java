package com.onegini.mobile.sdk.reactnative.handlers;

import com.onegini.mobile.sdk.android.handlers.error.OneginiInitializationError;

//Does it has to be java file?
public interface InitializationHandler {

  void onSuccess();

  void onError(OneginiInitializationError error);
}
