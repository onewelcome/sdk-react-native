package com.onegini.mobile.sdk.reactnative

object Constants {
    enum class PinFlow {
        Authentication,
        Create,
    }

    // Pin notification actions for RN Bridge
    const val ONEWELCOME_PIN_NOTIFICATION = "ONEWELCOME_PIN_NOTIFICATION"
    const val PIN_NOTIFICATION_OPEN_VIEW = "open"
    const val PIN_NOTIFICATION_CLOSE_VIEW = "close"
    const val PIN_NOTIFICATION_INCORRECT_PIN = "incorrectPin"
    const val PIN_NOTIFICATION_PIN_NOT_ALLOWED = "pinNotAllowed"

    // Pin actions for RN Bridge
    const val PIN_ACTION_CANCEL = "cancel"
    const val PIN_ACTION_PROVIDE = "provide"

    // Registration
    const val REGISTRATION_NOTIFICATION = "ONEWELCOME_REGISTRATION_NOTIFICATION"

    // Custom registration
    const val CUSTOM_REGISTRATION_NOTIFICATION = "ONEWELCOME_CUSTOM_REGISTRATION_NOTIFICATION"
    const val CUSTOM_REGISTRATION_NOTIFICATION_INIT_REGISTRATION = "initRegistration"
    const val CUSTOM_REGISTRATION_NOTIFICATION_FINISH_REGISTRATION = "finishRegistration"

    // Custom registration actions for RN Bridge
    const val CUSTOM_REGISTRATION_ACTION_CANCEL = "cancel"
    const val CUSTOM_REGISTRATION_ACTION_PROVIDE = "provide"

    const val MOBILE_AUTH_OTP_NOTIFICATION = "ONEWELCOME_MOBILE_AUTH_OTP_NOTIFICATION"
    const val MOBILE_AUTH_OTP_START_AUTHENTICATION = "startAuthentication"
    const val MOBILE_AUTH_OTP_FINISH_AUTHENTICATION = "finishAuthentication"

    const val ONEWELCOME_FINGERPRINT_NOTIFICATION = "ONEWELCOME_FINGERPRINT_NOTIFICATION"
    const val FINGERPRINT_NOTIFICATION_START_AUTHENTICATION = "startAuthentication"
    const val FINGERPRINT_NOTIFICATION_ON_NEXT_AUTHENTICATION_ATTEMPT = "onNextAuthenticationAttempt"
    const val FINGERPRINT_NOTIFICATION_ON_FINGERPRINT_CAPTURED = "onFingerprintCaptured"
    const val FINGERPRINT_NOTIFICATION_FINISH_AUTHENTICATION = "finishAuthentication"

    const val httpConnectTimeoutBrowserRegistrationMiliseconds = 5000
    const val httpReadTimeoutBrowserRegistrationMiliseconds = 20000
}
