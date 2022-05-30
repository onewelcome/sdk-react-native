package com.onegini.mobile.sdk.reactnative.model;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class ApplicationDetails {

  @SerializedName("application_identifier")
  private String applicationIdentifier;
  @SerializedName("application_platform")
  private String applicationPlatform;
  @SerializedName("application_version")
  private String applicationVersion;

  public String getApplicationIdentifier() {
    return applicationIdentifier;
  }

  public String getApplicationPlatform() {
    return applicationPlatform;
  }

  public String getApplicationVersion() {
    return applicationVersion;
  }
}
