
package com.onegini.mobile.sdk.reactnative.model;

import com.google.gson.annotations.SerializedName;

public class ImplicitUserDetails {

  @SuppressWarnings("unused")
  @SerializedName("decorated_user_id")
  private String decoratedUserId;

  @Override
  public String toString() {
    return decoratedUserId;
  }
}
