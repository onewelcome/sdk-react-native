package com.onegini.mobile;

import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.onegini.mobile.helpers.OneginiClientInitializer;
import com.onegini.mobile.helpers.RegistrationHelper;
import com.onegini.mobile.sdk.android.handlers.OneginiRegistrationHandler;
import com.onegini.mobile.sdk.android.handlers.error.OneginiRegistrationError;
import com.onegini.mobile.sdk.android.model.OneginiIdentityProvider;
import com.onegini.mobile.sdk.android.model.entity.CustomInfo;
import com.onegini.mobile.sdk.android.model.entity.UserProfile;
import com.onegini.mobile.view.handlers.BridgePinNotificationHandler;
import com.onegini.mobile.view.handlers.CreatePinRequestHandler;
import com.onegini.mobile.view.handlers.InitializationHandler;
import com.onegini.mobile.view.handlers.PinAuthenticationRequestHandler;

import java.util.Set;

import javax.annotation.Nullable;

import static com.onegini.mobile.Constants.PIN_ACTION_CANCEL;
import static com.onegini.mobile.Constants.PIN_ACTION_PROVIDE;
import static com.onegini.mobile.Constants.PIN_NOTIFICATION_CLOSE_VIEW;
import static com.onegini.mobile.Constants.PIN_NOTIFICATION_CONFIRM_VIEW;
import static com.onegini.mobile.Constants.PIN_NOTIFICATION_OPEN_VIEW;
import static com.onegini.mobile.Constants.PIN_NOTIFICATION_SHOW_ERROR;

public class RNOneginiSdkModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private final RegistrationHelper registrationHelper;

    private final static String ONEGINI_PIN_NOTIFICATION = "ONEGINI_PIN_NOTIFICATION";
    private final static String LOG_TAG = "RNOneginiSdk";

    private String configModelClassName = null;
    private String securityControllerClassName = "com.onegini.mobile.SecurityController";

    public static BridgePinNotificationHandler pinNotificationHandler;

    public RNOneginiSdkModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.registrationHelper = new RegistrationHelper();
        pinNotificationHandler = this.createBridgePinNotificationHandler();
    }

    @Override
    public boolean canOverrideExistingModule() {
        return true;
    }

    @Override
    public String getName() {
        return "RNOneginiSdk";
    }

    // React methods will be below


    @ReactMethod
    public void setConfigModelClassName(String configModelClassName) {
        this.configModelClassName = configModelClassName;
    }

    @ReactMethod
    public void setSecurityControllerClassName(String securityControllerClassName) {
        this.securityControllerClassName = securityControllerClassName;
    }

    @ReactMethod
    public void startClient(Callback callback) {
        final OneginiClientInitializer oneginiClientInitializer = new OneginiClientInitializer(this.reactContext, this.configModelClassName, this.securityControllerClassName);
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

    @ReactMethod
    public void submitPinAction(String action, Boolean isCreatePinFlow, @Nullable String pin) {
        switch (action){
            case PIN_ACTION_PROVIDE:
                if (isCreatePinFlow) {
                    CreatePinRequestHandler.CALLBACK.onPinProvided(pin.toCharArray());
                } else {
                    PinAuthenticationRequestHandler.CALLBACK.acceptAuthenticationRequest(pin.toCharArray());
                }
                break;
            case PIN_ACTION_CANCEL:
                if (isCreatePinFlow) {
                    CreatePinRequestHandler.CALLBACK.pinCancelled();
                } else {
                    PinAuthenticationRequestHandler.CALLBACK.denyAuthenticationRequest();
                }
                break;
            default:
                Log.e(LOG_TAG, "Got unsupported PIN action: " + action);
                break;
        }
    }

    private BridgePinNotificationHandler createBridgePinNotificationHandler(){
        return new BridgePinNotificationHandler(){
            @Override
            public void onNotify(final String event, final Boolean isCreatePinFlow){

                WritableMap data = Arguments.createMap();

                switch (event){
                    case PIN_NOTIFICATION_OPEN_VIEW:
                        data.putString("action", PIN_NOTIFICATION_OPEN_VIEW);
                        data.putBoolean("isCreatePinFlow", isCreatePinFlow);
                        break;
                    case PIN_NOTIFICATION_CONFIRM_VIEW:
                        data.putString("action", PIN_NOTIFICATION_CONFIRM_VIEW);
                        break;
                    case PIN_NOTIFICATION_CLOSE_VIEW:
                        data.putString("action", PIN_NOTIFICATION_CLOSE_VIEW);
                        break;
                    default:
                        Log.e(LOG_TAG, "Got unsupported PIN notification type: " + event);
                        break;

                }

                if(data.getString("action") != null){
                    getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(ONEGINI_PIN_NOTIFICATION, data);
                }
            }

            @Override
            public void onError(final String message){
                WritableMap data = Arguments.createMap();
                data.putString("action", PIN_NOTIFICATION_SHOW_ERROR);
                data.putString("errorMsg", message);

                getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(ONEGINI_PIN_NOTIFICATION, data);
            }
        };
    }

}
