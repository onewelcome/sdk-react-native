package com.onegini.mobile.sdk.reactnative.exception

enum class OneginiWrapperErrors(val code: String, val message: String) {
    USER_PROFILE_IS_NULL("8001", "user profile is null"),
    AUTHENTICATED_USER_PROFILE_IS_NULL("8002", "authenticated user profile is null"),
    AUTHENTICATOR_IS_NULL("8003", "authenticator is null"),
    REGISTERED_AUTHENTICATOR_IS_NULL("8004", "registered authenticators is null"),
    IDENTITY_PROVIDER_NOT_FOUND("8005", "identity provider not found"),
    QR_CODE_NOT_HAVE_DATA("8006", "Qr code does not contain required data."),
    METHOD_TO_CALL_NOT_FOUND("8007", "method to call not found"),
    URL_CANT_BE_NULL("8008", "url can`t be null"),
    URL_IS_NOT_WEB_PATH("8009", "incorrect url format"),
    PREFERRED_AUTHENTICATOR_ERROR("8010", "something went wrong"),
    WRONG_CONFIG_MODEL("8011", "Provided config model parameters are wrong"),
    MOBILE_AUTH_OTP_IS_DISABLED("8012", "The Mobile auth Otp is disabled"),
    ATTEMPT_COUNTER_ERROR("8013", "Wrong pin provided")
}
