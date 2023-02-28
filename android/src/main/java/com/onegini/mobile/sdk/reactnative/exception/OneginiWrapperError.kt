package com.onegini.mobile.sdk.reactnative.exception

enum class OneginiWrapperError(val code: Int, val message: String) {
    IDENTITY_PROVIDER_NOT_FOUND(8001, "Identity provider not found"),
    WRONG_PIN_ERROR(8004, "Wrong pin provided"),
    RESOURCE_CALL_ERROR(8005, "Resource call finished with error"),
    AUTHENTICATOR_DOES_NOT_EXIST(8006, "The authenticator does not exist"),
    PROFILE_DOES_NOT_EXIST(8007, "The profileId does not exist"),
    PARAMETERS_NOT_CORRECT(8009, "The parameters to the called function are not correct"),
    REGISTRATION_NOT_IN_PROGRESS(8010, "Registration is currently not in progress"),
    NO_PROFILE_AUTHENTICATED(8012, "No profile is currently authenticated"),
    MOBILE_AUTH_OTP_NOT_IN_PROGRESS(8013, "There is currently no mobile authentication in progress"),
    AUTHENTICATION_NOT_IN_PROGRESS(8015, "Authentication is currently not in progress"),
    PIN_CREATION_NOT_IN_PROGRESS(8016, "Pin creation is currently not in progress"),
    ACTION_NOT_ALLOWED(8017, "This action is currently not allowed"),
    INCORRECT_PIN_FLOW(8018, "Incorrect pinFlow supplied"),
    AUTHENTICATOR_NOT_REGISTERED(8019, "The authenticator is not registered"),
    SDK_NOT_STARTED(8020, "The SDK has not been started, use startClient to start it"),
    USER_NOT_AUTHENTICATED(9010, "No user is currently authenticated, possibly due to the fact that the access token has expired. A user must be authenticated in order to fetch resources."),
}
