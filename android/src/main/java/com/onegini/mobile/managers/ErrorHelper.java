package com.onegini.mobile.managers;

import android.content.Context;

import com.onegini.mobile.sdk.android.handlers.error.OneginiLogoutError;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;
import com.onegini.mobile.util.DeregistrationUtil;

public class ErrorHelper {
    public static String handleLogoutError(final OneginiLogoutError oneginiLogoutError, final UserProfile userProfile, Context packageContext) {
        @OneginiLogoutError.LogoutErrorType final int errorType = oneginiLogoutError.getErrorType();

        if (errorType == OneginiLogoutError.DEVICE_DEREGISTERED) {
            new DeregistrationUtil(packageContext).onDeviceDeregistered();
        } else if (errorType == OneginiLogoutError.USER_DEREGISTERED) {
            new DeregistrationUtil(packageContext).onUserDeregistered(userProfile);
        }

        return oneginiLogoutError.getMessage();
    }
}
