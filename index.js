import { NativeModules, Platform, NativeEventEmitter, DeviceEventEmitter } from 'react-native';
import { ONEGINI_SDK_CONFIG } from "./js/config";

const { RNOneginiSdk } = NativeModules;

const OneginiEventEmitter =
  Platform.OS === 'ios'
    ? new NativeEventEmitter(RNOneginiSdk)
    : DeviceEventEmitter;

export const ONEGINI_SDK_EVENTS = {
  ONEGINI_PIN_NOTIFICATION: 'ONEGINI_PIN_NOTIFICATION',
  ONEGINI_CUSTOM_REGISTRATION_NOTIFICATION: 'ONEGINI_CUSTOM_REGISTRATION_NOTIFICATION',
  ONEGINI_MOBILE_AUTH_OTP_NOTIFICATION: 'ONEGINI_MOBILE_AUTH_OTP_NOTIFICATION'
};

export const ONEGINI_PIN_NOTIFICATIONS = {
  OPEN: 'open',
  CONFIRM: 'confirm',
  CLOSE: 'close',
  ERROR: 'show_error',
  AUTH_ATTEMPT: 'auth_attempt',
  CHANGED: 'changed'
};

export const MOBILE_AUTH_OTP_NOTIFICATION = {
  START_AUTHENTICATION: 'startAuthentication',
  FINISH_AUTHENTICATION: 'finishAuthentication',
};

export const ONEGINI_PIN_ACTIONS = {
  PROVIDE_PIN: 'provide',
  CHANGE: 'change',
  CANCEL: 'cancel',
};

export const ONEGINI_PIN_FLOW = {
  AUTHENTICATION: 'authentication',
  CREATE: 'create',
  CHANGE: 'change',
};

const OneginiSdk = {};


OneginiSdk.listeners = {
  [ONEGINI_SDK_EVENTS.ONEGINI_PIN_NOTIFICATION]: null, // fires ONEGINI_PIN_NOTIFICATIONS
  [ONEGINI_SDK_EVENTS.ONEGINI_CUSTOM_REGISTRATION_NOTIFICATION]: null,
  [ONEGINI_SDK_EVENTS.ONEGINI_MOBILE_AUTH_OTP_NOTIFICATION]: null,
};

// eventType = ONEGINI_SDK_EVENTS
OneginiSdk.addEventListener = function (eventType, cb) {
  if (this.listeners[eventType]) {
    this.removeEventListener(eventType);
  }

  this.listeners[eventType] = OneginiEventEmitter.addListener(
    eventType,
    (item) => {
      cb(item);
    },
  );
};


// eventType = ONEGINI_SDK_EVENTS
OneginiSdk.removeEventListener = function (eventType) {
  if (this.listeners[eventType]) {
    this.listeners[eventType].remove();
    this.listeners[eventType] = null;
  }
};

OneginiSdk.startClient = function () {
  return new Promise((resolve) =>
    RNOneginiSdk.startClient(ONEGINI_SDK_CONFIG, (response) => resolve(response)),
  );
};

OneginiSdk.getIdentityProviders = function () {
  return RNOneginiSdk.getIdentityProviders();
};

OneginiSdk.getAccessToken = function () {
  return RNOneginiSdk.getAccessToken();
};

OneginiSdk.enrollMobileAuthentication = function () {
  return RNOneginiSdk.enrollMobileAuthentication();
};

OneginiSdk.submitAcceptMobileAuthOtp = function () {
  return RNOneginiSdk.submitAcceptMobileAuthOtp()
}

OneginiSdk.submitDenyMobileAuthOtp = function () {
  return RNOneginiSdk.submitDenyMobileAuthOtp()
}

OneginiSdk.handleMobileAuthWithOtp = function (otpCode) {
  return RNOneginiSdk.handleMobileAuthWithOtp(otpCode)
}

OneginiSdk.getAuthenticatedUserProfile = function () {
  return RNOneginiSdk.getAuthenticatedUserProfile()
}

//@todo will return profileId -> Later check out whole profile + don't forget to ask for userName on RN side
OneginiSdk.registerUser = function (identityProvider = null) {
  return new Promise((resolve) =>
    RNOneginiSdk.registerUser(identityProvider, (response) =>
      resolve(response),
    ),
  );
};

OneginiSdk.deregisterUser = function (profileId) {
  RNOneginiSdk.deregisterUser(profileId);
}

OneginiSdk.getUserProfiles = function () {
  return RNOneginiSdk.getUserProfiles();
}

OneginiSdk.getRedirectUri = function () {
  return new Promise((resolve) =>
    RNOneginiSdk.getRedirectUri((response) => resolve(response)),
  );
};

OneginiSdk.handleRegistrationCallback = function (uri) {
  RNOneginiSdk.handleRegistrationCallback(uri);
}

OneginiSdk.cancelRegistration = function () {
  RNOneginiSdk.cancelRegistration();
}

OneginiSdk.setConfigModelClassName = function (className = null) {
  RNOneginiSdk.setConfigModelClassName(className);
}

OneginiSdk.setSecurityControllerClassName = function (className = null) {
  RNOneginiSdk.setSecurityControllerClassName(className);
}


OneginiSdk.submitCustomRegistrationReturnSuccess = function (identityProviderId, result = null) {
  RNOneginiSdk.submitCustomRegistrationReturnSuccess(
    identityProviderId,
    result,
  );
}

OneginiSdk.submitCustomRegistrationReturnError = function (identityProviderId, result = null) {
  RNOneginiSdk.submitCustomRegistrationReturnError(
    identityProviderId,
    result,
  );
}

OneginiSdk.submitPinAction = function (flow, action, pin = null) {
  RNOneginiSdk.submitPinAction(flow, action, pin);
}

OneginiSdk.submitCreatePinAction = function (action, pin = null) {
  RNOneginiSdk.submitCreatePinAction(action, pin);
}

OneginiSdk.submitChangePinAction = function (action, pin = null) {
  RNOneginiSdk.submitChangePinAction(action, pin);
}

OneginiSdk.submitAuthenticationPinAction = function (action, pin = null) {
  RNOneginiSdk.submitAuthenticationPinAction(action, pin);
}

OneginiSdk.authenticateUser = function (profileId) { return RNOneginiSdk.authenticateUser(profileId); }

OneginiSdk.logout = function () {
  return new Promise((resolve) =>
    RNOneginiSdk.logout((response) => resolve(response)),
  );
};


//Fingerprint
OneginiSdk.registerFingerprintAuthenticator = function (profileId) {
  RNOneginiSdk.registerFingerprintAuthenticator(profileId);
}

OneginiSdk.deregisterFingerprintAuthenticator = function (profileId) {
  RNOneginiSdk.deregisterFingerprintAuthenticator(profileId);
}

OneginiSdk.isFingerprintAuthenticatorRegistered = function (profileId) {
  RNOneginiSdk.isFingerprintAuthenticatorRegistered(profileId);
}

export default OneginiSdk;
