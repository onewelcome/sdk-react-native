package com.onegini.mobile;

public interface Constants {
  String[] DEFAULT_SCOPES = { "read" };

  // Pin notification actions for RN Bridge
  String PIN_NOTIFICATION_OPEN_VIEW = "open";
  String PIN_NOTIFICATION_CONFIRM_VIEW = "confirm";
  String PIN_NOTIFICATION_CLOSE_VIEW = "close";
  String PIN_NOTIFICATION_SHOW_ERROR = "show_error";
  String PIN_NOTIFICATION_AUTH_ATTEMPT = "auth_attempt";

  // Pin actions for RN Bridge
  String PIN_ACTION_CANCEL = "cancel";
  String PIN_ACTION_PROVIDE = "provide";
}
