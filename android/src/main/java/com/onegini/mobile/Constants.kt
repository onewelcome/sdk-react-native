package com.onegini.mobile

object Constants {
    // Pins flow
    enum class PinFlow(val flowString: String) {
        Authentication("authentication"), Create("create"), Change("change");

        companion object {
            @Throws(Exception::class)
            fun parse(flowString: String?): PinFlow {
                return when (flowString) {
                    Authentication.flowString -> {
                        Authentication
                    }
                    Create.flowString -> {
                        Create
                    }
                    Change.flowString -> {
                        Change
                    }
                    else -> {
                        throw Exception("The Flow$flowString does not extis")
                    }
                }
            }
        }
    }

    val DEFAULT_SCOPES = arrayOf("read")
    const val NEW_LINE = "\n"
    const val FCM_SENDER_ID = "586427927998"
    const val EXTRA_COMMAND = "command"
    const val COMMAND_START = "start"
    const val COMMAND_FINISH = "finish"
    const val COMMAND_SHOW_SCANNING = "show"
    const val COMMAND_RECEIVED_FINGERPRINT = "received"
    const val COMMAND_ASK_TO_ACCEPT_OR_DENY = "ask"

    // Pin notification actions for RN Bridge
    const val ONEGINI_PIN_NOTIFICATION = "ONEGINI_PIN_NOTIFICATION"
    const val PIN_NOTIFICATION_OPEN_VIEW = "open"
    const val PIN_NOTIFICATION_CONFIRM_VIEW = "confirm"
    const val PIN_NOTIFICATION_CLOSE_VIEW = "close"
    const val PIN_NOTIFICATION_SHOW_ERROR = "show_error"
    const val PIN_NOTIFICATION_CHANGED = "changed"

    // Pin actions for RN Bridge
    const val PIN_ACTION_CANCEL = "cancel"
    const val PIN_ACTION_PROVIDE = "provide"

    // Custom registration
    const val CUSTOM_REGISTRATION_NOTIFICATION = "ONEGINI_CUSTOM_REGISTRATION_NOTIFICATION"
    const val CUSTOM_REGISTRATION_NOTIFICATION_INIT_REGISTRATION = "initRegistration"
    const val CUSTOM_REGISTRATION_NOTIFICATION_FINISH_REGISTRATION = "finishRegistration"

    const val MOBILE_AUTH_OTP_NOTIFICATION = "ONEGINI_MOBILE_AUTH_OTP_NOTIFICATION"
    const val MOBILE_AUTH_OTP_START_AUTHENTICATION = "startAuthentication"
    const val MOBILE_AUTH_OTP_FINISH_AUTHENTICATION = "finishAuthentication"

    const val ONEGINI_FINGERPRINT_NOTIFICATION = "ONEGINI_FINGERPRINT_NOTIFICATION"
    const val FINGERPRINT_NOTIFICATION_START_AUTHENTICATION = "startAuthentication"
    const val FINGERPRINT_NOTIFICATION_ON_NEXT_AUTHENTICATION_ATTEMPT = "onNextAuthenticationAttempt"
    const val FINGERPRINT_NOTIFICATION_ON_FINGERPRINT_CAPTURED = "onFingerprintCaptured"
    const val FINGERPRINT_NOTIFICATION_FINISH_AUTHENTICATION = "finishAuthentication"
}