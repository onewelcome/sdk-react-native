//@todo Later will be transferred to RN Wrapper
package com.onegini.mobile.util;

import android.content.Context;
import android.util.Log;
import com.onegini.mobile.storage.UserStorage;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;

public class DeregistrationUtil {

  private final Context context;

  public DeregistrationUtil(Context context) {
    this.context = context;
  }

  public void onUserDeregistered(UserProfile userProfile) {
    if (userProfile == null) {
      Log.e("DeregistrationUtil", "userProfile == null");
      return;
    }
    new UserStorage(context).removeUser(userProfile);
  }

  public void onDeviceDeregistered() {
    new UserStorage(context).clearStorage();
  }
}
