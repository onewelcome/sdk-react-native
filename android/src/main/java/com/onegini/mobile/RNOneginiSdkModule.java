package com.onegini.mobile;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.onegini.mobile.helpers.OneginiClientInitializer;
import com.onegini.mobile.helpers.RegistrationHelper;
import com.onegini.mobile.view.handlers.InitializationHandler;
import com.onegini.mobile.sdk.android.handlers.OneginiRegistrationHandler;
import com.onegini.mobile.sdk.android.handlers.error.OneginiRegistrationError;
import com.onegini.mobile.sdk.android.model.OneginiIdentityProvider;
import com.onegini.mobile.sdk.android.model.entity.CustomInfo;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;

import java.util.Set;
import javax.annotation.Nullable;

public class RNOneginiSdkModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;
  private final RegistrationHelper registrationHelper;

  public RNOneginiSdkModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    this.registrationHelper = new RegistrationHelper();
  }

  @Override
  public String getName() {
    return "RNOneginiSdk";
  }

  // React methods will be below
  @ReactMethod
  public void startClient(Callback callback) {
    final OneginiClientInitializer oneginiClientInitializer = new OneginiClientInitializer(this.reactContext);
    oneginiClientInitializer.startOneginiClient(new InitializationHandler() {

      @Override
      public void onSuccess() {
        WritableMap result = Arguments.createMap();
        result.putBoolean("success", true);

        callback.invoke(result);
      }

      @Override
      public void onError(final String errorMessage) {
        WritableMap result = Arguments.createMap();
        result.putBoolean("success", false);
        result.putString("errorMsg", errorMessage);

        callback.invoke(result);
      }
    });
  }

  @ReactMethod
  public void getIdentityProviders(Callback callback) {
    final Set<OneginiIdentityProvider> identityProviders = OneginiSDK.getOneginiClient(this.reactContext).getUserClient().getIdentityProviders();

    WritableArray result = Arguments.createArray();

    for (OneginiIdentityProvider provider : identityProviders) {
      WritableMap providerMap = Arguments.createMap();

      providerMap.putString("id", provider.getId());
      providerMap.putString("name", provider.getName());

      result.pushMap(providerMap);
    }

    callback.invoke(result);
  }

  @ReactMethod
  public void registerUser(String identityProviderId, Callback callback) {
    registrationHelper.registerUser(this.reactContext, null, new OneginiRegistrationHandler() {

      @Override
      public void onSuccess(final UserProfile userProfile, final CustomInfo customInfo) {
        WritableMap result = Arguments.createMap();
        result.putBoolean("success", true);
        result.putString("profileId", userProfile.getProfileId());

        callback.invoke(result);
      }

      @Override
      public void onError(final OneginiRegistrationError oneginiRegistrationError) {
        @OneginiRegistrationError.RegistrationErrorType final int errorType = oneginiRegistrationError.getErrorType();
        @Nullable String errorMessage = registrationHelper.getErrorMessageByCode(errorType);

        if (errorMessage == null) {
          errorMessage = oneginiRegistrationError.getMessage();
        }

        WritableMap result = Arguments.createMap();
        result.putBoolean("success", false);
        result.putString("errorMsg", errorMessage);

        callback.invoke(result);
      }
    });
  }

  @ReactMethod
  public void getRedirectUri(Callback callback) {
    String uri = registrationHelper.getRedirectUri(this.reactContext);

    WritableMap result = Arguments.createMap();
    result.putBoolean("success", true);
    result.putString("redirectUri", uri);

    callback.invoke(result);
  }

  @ReactMethod
  public void handleRegistrationCallback(String uri) {
    registrationHelper.handleRegistrationCallback(uri);
  }

  @ReactMethod
  public void cancelRegistration() {
    registrationHelper.cancelRegistration();
  }
}
