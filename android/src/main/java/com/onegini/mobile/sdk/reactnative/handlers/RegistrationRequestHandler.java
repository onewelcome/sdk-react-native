//@todo Later will be transferred to RN Wrapper
package com.onegini.mobile.sdk.reactnative.handlers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.onegini.mobile.sdk.android.handlers.request.OneginiBrowserRegistrationRequestHandler;
import com.onegini.mobile.sdk.android.handlers.request.callback.OneginiBrowserRegistrationCallback;

public class RegistrationRequestHandler implements OneginiBrowserRegistrationRequestHandler {

    private OneginiBrowserRegistrationCallback callback;

    /**
     * Finish registration action with result from web browser
     */
    public void handleRegistrationCallback(final Uri uri) {
        if (callback != null) {
            callback.handleRegistrationCallback(uri);
            callback = null;
        }
    }

    /**
     * Cancel registration action in case of web browser error
     */
    public void onRegistrationCanceled() {
        if (callback != null) {
            callback.denyRegistration();
            callback = null;
        }
    }

    private final Context context;

    public RegistrationRequestHandler(final Context context) {
        this.context = context;
    }

    @Override
    public void startRegistration(final Uri uri, final OneginiBrowserRegistrationCallback oneginiBrowserRegistrationCallback) {
        callback = oneginiBrowserRegistrationCallback;

        // We're going to launch external browser to allow user to log in. You could also use embedded WebView instead.
        final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        context.startActivity(intent);
    }
}
