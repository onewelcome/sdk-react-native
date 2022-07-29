//@todo Later will be transferred to RN Wrapper
package com.onegini.mobile.sdk.reactnative.model;

import com.onegini.mobile.sdk.android.model.entity.UserProfile;

public class User {

  private final String name;
  private final UserProfile userProfile;

  public User(final UserProfile userProfile, final String name) {
    this.userProfile = userProfile;
    this.name = name;
  }

  public UserProfile getUserProfile() {
    return userProfile;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name + " (id: " + userProfile.getProfileId() + ")";
  }
}
