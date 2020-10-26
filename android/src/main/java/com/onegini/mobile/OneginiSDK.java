package com.onegini.mobile;

import java.util.concurrent.TimeUnit;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import android.content.Context;

import javax.annotation.Nullable;

import com.onegini.mobile.view.handlers.CreatePinRequestHandler;
import com.onegini.mobile.view.handlers.PinAuthenticationRequestHandler;
import com.onegini.mobile.view.handlers.RegistrationRequestHandler;
import com.onegini.mobile.sdk.android.client.OneginiClient;
import com.onegini.mobile.sdk.android.client.OneginiClientBuilder;
import com.onegini.mobile.sdk.android.model.OneginiClientConfigModel;

public class OneginiSDK {

    private static @Nullable String configModelClassName;
    private static @Nullable String securityControllerClassName;

    public static void setConfigModelClassName(@Nullable String configModelClassName) {
        OneginiSDK.configModelClassName = configModelClassName;
    }

    public static void setSecurityControllerClassName(@Nullable String securityControllerClassName) {
        OneginiSDK.securityControllerClassName = securityControllerClassName;
    }

    public static OneginiClient getOneginiClient(final Context context) {
        OneginiClient oneginiClient = OneginiClient.getInstance();
        if (oneginiClient == null) {
            oneginiClient = buildSDK(context);
        }
        return oneginiClient;
    }

    private static OneginiClient buildSDK(final Context context) {
        final Context applicationContext = context.getApplicationContext();
        final RegistrationRequestHandler registrationRequestHandler = new RegistrationRequestHandler(applicationContext);
        final PinAuthenticationRequestHandler pinAuthenticationRequestHandler = new PinAuthenticationRequestHandler(applicationContext);
        final CreatePinRequestHandler createPinRequestHandler = new CreatePinRequestHandler(applicationContext);

        // will throw OneginiConfigNotFoundException if OneginiConfigModel class can't be found
        OneginiClientBuilder clientBuilder = new OneginiClientBuilder(applicationContext, createPinRequestHandler, pinAuthenticationRequestHandler)
                // handlers for optional functionalities
                .setBrowserRegistrationRequestHandler(registrationRequestHandler)
                // Set http connect / read timeout
                .setHttpConnectTimeout((int) TimeUnit.SECONDS.toMillis(5))
                .setHttpReadTimeout((int) TimeUnit.SECONDS.toMillis(20));

        // Set config model
        setConfigModel(clientBuilder);

        // Set security controller
        setSecurityController(clientBuilder);

        return clientBuilder.build();
    }

    private static void setConfigModel(OneginiClientBuilder clientBuilder) {
        if (configModelClassName == null) {
            return;
        }

        try {
            Class<?> clazz = Class.forName(configModelClassName);
            Constructor<?> ctor = clazz.getConstructor();
            Object object = ctor.newInstance();

            if (object instanceof OneginiClientConfigModel) {
                clientBuilder.setConfigModel((OneginiClientConfigModel) object);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static void setSecurityController(OneginiClientBuilder clientBuilder) {
        if (securityControllerClassName == null) {
            return;
        }

        try {
            Class<?> securityController = Class.forName(securityControllerClassName);
            clientBuilder.setSecurityController(securityController);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
