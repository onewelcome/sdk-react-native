package com.onegini.mobile.sdk.reactnative.exception

enum class OneginiWrapperErrors(val code: String, val message: String) {
    IDENTITY_PROVIDER_NOT_FOUND("8001", "Identity provider not found"),
    WRONG_CONFIG_MODEL("8003", "Provided config model parameters are wrong"),
    MOBILE_AUTH_OTP_IS_DISABLED("8004", "The Mobile auth Otp is disabled"),
    WRONG_PIN_ERROR("8005", "Wrong pin provided"),
    RESOURCE_CALL_ERROR("8006", "Resource call finished with error"),
    FINGERPRINT_IS_NOT_ENABLED("8007", "The fingerprint is not enabled. Please check your configuration"),
    AUTHENTICATOR_DOES_NOT_EXIST("8008", "The Fingerprint authenticator does not exist"),
    PROFILE_DOES_NOT_EXIST("8009", "The profileId does not exist"),
    PIN_ERROR_NOT_EQUAL("8010", "PIN was not the same, choose PIN")
}
