package com.onegini.mobile.sdk.reactnative.exception

enum class OneginiWrapperErrors(val code: String, val message: String) {
    IDENTITY_PROVIDER_NOT_FOUND("8001", "Identity provider not found"),
    WRONG_CONFIG_MODEL("8002", "Provided config model parameters are wrong"),
    MOBILE_AUTH_OTP_IS_DISABLED("8003", "The Mobile auth Otp is disabled"),
    WRONG_PIN_ERROR("8004", "Wrong pin provided"),
    RESOURCE_CALL_ERROR("8005", "Resource call finished with error"),
    AUTHENTICATOR_DOES_NOT_EXIST("8006", "The Fingerprint authenticator does not exist"),
    PROFILE_DOES_NOT_EXIST("8007", "The profileId does not exist")
}
