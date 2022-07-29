package com.onegini.mobile.rnexampleapp;

import android.os.Build;
import com.onegini.mobile.sdk.android.model.OneginiClientConfigModel;

public class OneginiConfigModel implements OneginiClientConfigModel {

  private final String appIdentifier = "RNExampleApp";
  private final String appPlatform = "android";
  private final String redirectionUri = "reactnativeexample://loginsuccess";
  private final String appVersion = "0.2.0";
  private final String baseURL = "https://token-mobile.test.onegini.com";
  private final String resourceBaseURL = "https://token-mobile.test.onegini.com/resources/";
  private final String keystoreHash = "2007fdb41eb2444d48410bebe8c8cf5464b9ee3fc66aa768e15fb66f014cd5a2";

  public String getAppIdentifier() {
    return appIdentifier;
  }

  public String getAppPlatform() {
    return appPlatform;
  }

  public String getRedirectUri() {
    return redirectionUri;
  }

  public String getAppVersion() {
    return appVersion;
  }

  public String getBaseUrl() {
    return baseURL;
  }

  public String getResourceBaseUrl() {
    return resourceBaseURL;
  }

  public int getCertificatePinningKeyStore() {
    return R.raw.keystore;
  }

  public String getKeyStoreHash() {
    return keystoreHash;
  }

  public String getDeviceName() {
    return Build.BRAND + " " + Build.MODEL;
  }

  public String getServerPublicKey() {
    return null;
  }

  @Override
  public String toString() {
    return "ConfigModel{" +
            "  appIdentifier='" + appIdentifier + "'" +
            ", appPlatform='" + appPlatform + "'" +
            ", redirectionUri='" + redirectionUri + "'" +
            ", appVersion='" + appVersion + "'" +
            ", baseURL='" + baseURL + "'" +
            ", resourceBaseURL='" + resourceBaseURL + "'" +
            ", keyStoreHash='" + getKeyStoreHash() + "'" +
            "}";
  }
}
