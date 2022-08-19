//@todo Later will be transferred to RN Wrapper
package com.onegini.mobile.sdk.reactnative.handlers;

import com.onegini.mobile.sdk.android.handlers.error.OneginiInitializationError;

public interface InitializationHandler {

  void onSuccess();

  void onError(OneginiInitializationError error);
}
