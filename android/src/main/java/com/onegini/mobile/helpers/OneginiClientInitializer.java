package com.onegini.mobile.helpers;

import android.content.Context;

import javax.annotation.Nullable;

import com.onegini.mobile.OneginiSDK;
import com.onegini.mobile.storage.UserStorage;
import com.onegini.mobile.util.DeregistrationUtil;
import com.onegini.mobile.view.handlers.InitializationHandler;
import com.onegini.mobile.sdk.android.client.OneginiClient;
import com.onegini.mobile.sdk.android.handlers.OneginiInitializationHandler;
import com.onegini.mobile.sdk.android.handlers.error.OneginiInitializationError;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;

public class OneginiClientInitializer {

    private static boolean isInitialized;

    private final DeregistrationUtil deregistrationUtil;
    private final UserStorage userStorage;
    private final Context context;
    private final @Nullable String configModelClassName;
    private final @Nullable String securityControllerClassName;

    public OneginiClientInitializer(final Context context, @Nullable String configModelClassName, @Nullable String securityControllerClassName) {
        this.context = context;
        deregistrationUtil = new DeregistrationUtil(context);
        userStorage = new UserStorage(context);

        this.configModelClassName = configModelClassName;
        this.securityControllerClassName = securityControllerClassName;
    }

    public void startOneginiClient(final InitializationHandler initializationHandler) {
        if (!isInitialized) {
            start(initializationHandler);
        } else {
            initializationHandler.onSuccess();
        }
    }

    private void start(final InitializationHandler initializationHandler) {
        OneginiSDK.setConfigModelClassName(this.configModelClassName);
        OneginiSDK.setSecurityControllerClassName(this.securityControllerClassName);

        final OneginiClient oneginiClient = OneginiSDK.getOneginiClient(context);
        oneginiClient.start(new OneginiInitializationHandler() {
            @Override
            public void onSuccess(final Set<UserProfile> removedUserProfiles) {
                setInitialized();
                if (!removedUserProfiles.isEmpty()) {
                    removeUserProfiles(removedUserProfiles);
                }
                initializationHandler.onSuccess();
            }

            @Override
            public void onError(final OneginiInitializationError error) {
                @OneginiInitializationError.InitializationErrorType final int errorType = error.getErrorType();
                if (errorType == OneginiInitializationError.DEVICE_DEREGISTERED) {
                    deregistrationUtil.onDeviceDeregistered();
                }
                initializationHandler.onError(error.getMessage());
            }

            private void removeUserProfiles(final Set<UserProfile> removedUserProfiles) {
                for (final UserProfile userProfile : removedUserProfiles) {
                    userStorage.removeUser(userProfile);
                }
            }

            private synchronized void setInitialized() {
                isInitialized = true;
            }
        });
    }
}
