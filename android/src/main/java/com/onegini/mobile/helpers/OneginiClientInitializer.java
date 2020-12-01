//@todo Later will be transferred to RN Wrapper
package com.onegini.mobile.helpers;

import androidx.annotation.NonNull;

import com.onegini.mobile.OneginiSDK;
import com.onegini.mobile.model.rn.OneginiReactNativeConfig;
import com.onegini.mobile.sdk.android.client.OneginiClient;
import com.onegini.mobile.sdk.android.handlers.OneginiInitializationHandler;
import com.onegini.mobile.sdk.android.handlers.error.OneginiInitializationError;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;
import com.onegini.mobile.storage.UserStorage;
import com.onegini.mobile.util.DeregistrationUtil;
import com.onegini.mobile.view.handlers.InitializationHandler;

import java.util.Set;

import javax.annotation.Nullable;

public class OneginiClientInitializer {

    private static boolean isInitialized;

    private final DeregistrationUtil deregistrationUtil;
    private final UserStorage userStorage;
    @NonNull
    private final OneginiSDK oneginiSDK;
    private final @Nullable
    String configModelClassName;
    private final @Nullable
    String securityControllerClassName;

    public OneginiClientInitializer(@NonNull OneginiSDK oneginiSDK,
                                    @Nullable String configModelClassName,
                                    @Nullable String securityControllerClassName,
                                    @NonNull DeregistrationUtil deregistrationUtil,
                                    @NonNull UserStorage userStorage) {
        this.oneginiSDK = oneginiSDK;
        this.configModelClassName = configModelClassName;
        this.securityControllerClassName = securityControllerClassName;

        this.deregistrationUtil = deregistrationUtil;
        this.userStorage = userStorage;
    }

    public void startOneginiClient(@Nullable OneginiReactNativeConfig config, final InitializationHandler initializationHandler) {
        if (!isInitialized) {
            start(config, initializationHandler);
        } else {
            initializationHandler.onSuccess();
        }
    }

    private void start(@Nullable OneginiReactNativeConfig config, final InitializationHandler initializationHandler) {
        oneginiSDK.init(config, this.configModelClassName, this.securityControllerClassName);

        final OneginiClient oneginiClient = oneginiSDK.getOneginiClient();
        oneginiClient.start(new OneginiInitializationHandler() {
            @Override
            public void onSuccess(final Set<UserProfile> removedUserProfiles) {
                oneginiSDK.onStart();
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
