package com.onegini.mobile.helpers;

import android.content.Context;
import android.net.Uri;

import com.onegini.mobile.Constants;
import com.onegini.mobile.OneginiSDK;
import com.onegini.mobile.sdk.android.client.OneginiClient;
import com.onegini.mobile.sdk.android.handlers.OneginiRegistrationHandler;
import com.onegini.mobile.sdk.android.handlers.error.OneginiAuthenticationError;
import com.onegini.mobile.sdk.android.handlers.error.OneginiRegistrationError;
import com.onegini.mobile.sdk.android.model.OneginiIdentityProvider;
import com.onegini.mobile.view.handlers.RegistrationRequestHandler;

import javax.annotation.Nullable;


public class RegistrationHelper {

    public String getRedirectUri(Context packageContext) {
        final OneginiClient client = OneginiSDK.getOneginiClient(packageContext.getApplicationContext());

        return client.getConfigModel().getRedirectUri();
    }

    public void registerUser(Context packageContext, @Nullable final OneginiIdentityProvider identityProvider, OneginiRegistrationHandler registrationHandler) {
        final OneginiClient oneginiClient = OneginiSDK.getOneginiClient(packageContext);
        oneginiClient.getUserClient().registerUser(identityProvider, Constants.DEFAULT_SCOPES, registrationHandler);
    }

    public @Nullable
    String getErrorMessageByCode(@OneginiRegistrationError.RegistrationErrorType final int errorType) {
        @Nullable String errorMessage;

        switch (errorType) {
            case OneginiRegistrationError.DEVICE_DEREGISTERED:
                errorMessage = "The device was deregistered, please try registering again";
                //@todo Do not forget to deregister on RN side
                break;
            case OneginiRegistrationError.ACTION_CANCELED:
                errorMessage = "Registration was cancelled";
                break;
            case OneginiAuthenticationError.NETWORK_CONNECTIVITY_PROBLEM:
                errorMessage = "No internet connection.";
                break;
            case OneginiAuthenticationError.SERVER_NOT_REACHABLE:
                errorMessage = "The server is not reachable.";
                break;
            case OneginiRegistrationError.OUTDATED_APP:
                errorMessage = "Please update this application in order to use it.";
                break;
            case OneginiRegistrationError.OUTDATED_OS:
                errorMessage = "Please update your Android version to use this application.";
                break;
            case OneginiRegistrationError.INVALID_IDENTITY_PROVIDER:
                errorMessage = "The Identity provider you were trying to use is invalid.";
                break;
            case OneginiRegistrationError.CUSTOM_REGISTRATION_EXPIRED:
                errorMessage = "Custom registration request has expired. Please retry.";
                break;
            case OneginiRegistrationError.CUSTOM_REGISTRATION_FAILURE:
                errorMessage = "Custom registration request has failed, see logcat for more details.";
                break;
            case OneginiRegistrationError.GENERAL_ERROR:
                errorMessage = "General error";
                break;
            default:
                errorMessage = null;
                break;
        }

        return errorMessage;
    }

    public void handleRegistrationCallback(String uri) {
        RegistrationRequestHandler.handleRegistrationCallback(Uri.parse(uri));
    }

    public void cancelRegistration() {
        RegistrationRequestHandler.onRegistrationCanceled();
    }
}
