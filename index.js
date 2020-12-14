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
  ONEGINI_MOBILE_AUTH_OTP_NOTIFICATION: 'ONEGINI_MOBILE_AUTH_OTP_NOTIFICATION',
  ONEGINI_FINGERPRINT_NOTIFICATION: 'ONEGINI_FINGERPRINT_NOTIFICATION'
};

export const ONEGINI_PIN_NOTIFICATIONS = {
  OPEN: 'open',
  CONFIRM: 'confirm',
  CLOSE: 'close',
  ERROR: 'show_error',
  AUTH_ATTEMPT: 'auth_attempt',
  CHANGED: 'changed'
};

export const CUSTOM_REGISTRATION_NOTIFICATIONS = {
  INIT_REGISTRATION : 'initRegistration',
  FINISH_REGISTRATION: 'finishRegistration',
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

export const FINGERPRINT_NOTIFICATION = {
  START_AUTHENTICATION : "startAuthentication",
  ON_NEXT_AUTHENTICATION_ATTEMPT : "onNextAuthenticationAttempt",
  ON_FINGERPRINT_CAPTURED : "onFingerprintCaptured",
  FINISH_AUTHENTICATION : "finishAuthentication"
}

const OneginiSdk = {};

OneginiSdk.config = {
  customProviders: [{ id: '2-way-otp-api', isTwoStep: true }],
  enableMobileAuthenticationOtp: true,
  enableFingerprint: true
}

OneginiSdk.listeners = {
  [ONEGINI_SDK_EVENTS.ONEGINI_PIN_NOTIFICATION]: null, // fires ONEGINI_PIN_NOTIFICATIONS
  [ONEGINI_SDK_EVENTS.ONEGINI_CUSTOM_REGISTRATION_NOTIFICATION]: null,
  [ONEGINI_SDK_EVENTS.ONEGINI_MOBILE_AUTH_OTP_NOTIFICATION]: null,
  [ONEGINI_SDK_EVENTS.ONEGINI_FINGERPRINT_NOTIFICATION]: null,
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

OneginiSdk.startClient = function (sdkConfig = OneginiSdk.config) {
  return Platform.OS === 'ios'
      ? RNOneginiSdk.startClient()
      : RNOneginiSdk.startClient(sdkConfig)
};

OneginiSdk.getIdentityProviders = function () {
  return Platform.OS === 'ios'
    ? Promise.reject('Unfortunately this feature is not supported, yet.')
    : RNOneginiSdk.getIdentityProviders();
};

OneginiSdk.getAccessToken = function () {
  return Platform.OS === 'ios'
    ? Promise.reject('Unfortunately this feature is not supported, yet.')
    : RNOneginiSdk.getAccessToken();
};

OneginiSdk.enrollMobileAuthentication = function () {
  return Platform.OS === 'ios'
    ? Promise.reject({ message: 'Unfortunately this feature is not supported, yet.' })
    : RNOneginiSdk.enrollMobileAuthentication()
};

OneginiSdk.submitAcceptMobileAuthOtp = function () {
  return Platform.OS === 'ios'
    ? Promise.reject('Unfortunately this feature is not supported, yet.')
    : RNOneginiSdk.submitAcceptMobileAuthOtp()
}

OneginiSdk.submitDenyMobileAuthOtp = function () {
  return Platform.OS === 'ios'
    ? Promise.reject('Unfortunately this feature is not supported, yet.')
    : RNOneginiSdk.submitDenyMobileAuthOtp()
}

OneginiSdk.handleMobileAuthWithOtp = function (otpCode) {
  return Platform.OS === 'ios'
    ? Promise.reject({ message: 'Unfortunately this feature is not supported, yet.' })
    : RNOneginiSdk.handleMobileAuthWithOtp(otpCode)
}

OneginiSdk.getAuthenticatedUserProfile = function () {
  return Platform.OS === 'ios'
    ? Promise.reject({ message: 'Unfortunately this feature is not supported, yet.' })
    : RNOneginiSdk.getAuthenticatedUserProfile()
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
  return RNOneginiSdk.getRedirectUri()
};

OneginiSdk.handleRegistrationCallback = function (uri) {
  RNOneginiSdk.handleRegistrationCallback(uri);
}

OneginiSdk.cancelRegistration = function () {
  RNOneginiSdk.cancelRegistration();
}

OneginiSdk.setConfigModelClassName = function (className = null) {
  Platform.OS === 'ios'
    ? console.error('setConfigModelClassName is Android only method')
    : RNOneginiSdk.setConfigModelClassName(className);
}

OneginiSdk.setSecurityControllerClassName = function (className = null) {
  Platform.OS === 'ios'
    ? console.error('setSecurityControllerClassName is Android only method')
    : RNOneginiSdk.setSecurityControllerClassName(className);
}


OneginiSdk.submitCustomRegistrationReturnSuccess = function (identityProviderId, result = null) {
  Platform.OS === 'ios'
    ? Promise.reject('Unfortunately this feature is not supported, yet.')
    : RNOneginiSdk.submitCustomRegistrationReturnSuccess(
    identityProviderId,
    result,
  );
}

OneginiSdk.submitCustomRegistrationReturnError = function (identityProviderId, result = null) {
  return Platform.OS === 'ios'
    ? Promise.reject('Unfortunately this feature is not supported, yet.')
    : RNOneginiSdk.submitCustomRegistrationReturnError(
    identityProviderId,
    result,
  );
}

OneginiSdk.submitPinAction = function (flow, action, pin = null) {
  RNOneginiSdk.submitPinAction(flow, action, pin);
}

OneginiSdk.authenticateUser = function (profileId) {
  return Platform.OS === 'ios'
    ? Promise.reject('Unfortunately this feature is not supported, yet.')
    : RNOneginiSdk.authenticateUser(profileId);
}

OneginiSdk.getRegisteredAuthenticators = function (profileId) {
  return Platform.OS === 'ios'
    ? Promise.reject('Unfortunately this feature is not supported, yet.')
    : RNOneginiSdk.getRegisteredAuthenticators(profileId);
}

OneginiSdk.getAllAuthenticators = function (profileId) {
  return Platform.OS === 'ios'
    ? Promise.reject('Unfortunately this feature is not supported, yet.')
    : RNOneginiSdk.getAllAuthenticators(profileId);
}

OneginiSdk.getRegisteredAuthenticators = function (profileId) {
  return RNOneginiSdk.getRegisteredAuthenticators(profileId);
}

OneginiSdk.getAllAuthenticators = function (profileId) {
  return RNOneginiSdk.getAllAuthenticators(profileId);
}

OneginiSdk.logout = function () {
  return new Promise((resolve) =>
    RNOneginiSdk.logout((response) => resolve(response)),
  );
};

OneginiSdk.setPreferredAuthenticator = function (profileId, idOneginiAuthenticator) {
  return Platform.OS === 'ios'
    ? Promise.reject({ message: 'Unfortunately this feature is not supported, yet.' })
    : RNOneginiSdk.setPreferredAuthenticator(profileId, idOneginiAuthenticator);
}

//Fingerprint
OneginiSdk.registerFingerprintAuthenticator = function (profileId) {
  return Platform.OS === 'ios'
    ? Promise.reject({ message: 'Unfortunately this feature is not supported, yet.' })
    : RNOneginiSdk.registerFingerprintAuthenticator(profileId);
}

OneginiSdk.deregisterFingerprintAuthenticator = function (profileId) {
  return Platform.OS === 'ios'
    ? Promise.reject('Unfortunately this feature is not supported, yet.')
    : RNOneginiSdk.deregisterFingerprintAuthenticator(profileId);
}

OneginiSdk.isFingerprintAuthenticatorRegistered = function (profileId) {
  return Platform.OS === 'ios'
    ? Promise.reject('Unfortunately this feature is not supported, yet.')
    : RNOneginiSdk.isFingerprintAuthenticatorRegistered(profileId);
}

OneginiSdk.submitFingerprintAcceptAuthenticationRequest = function () {
  return Platform.OS === 'ios'
    ? Promise.reject('Unfortunately this feature is not supported, yet.')
    : RNOneginiSdk.submitFingerprintAcceptAuthenticationRequest();
}

OneginiSdk.submitFingerprintDenyAuthenticationRequest = function () {
  return Platform.OS === 'ios'
    ? Promise.reject('Unfortunately this feature is not supported, yet.')
    : RNOneginiSdk.submitFingerprintDenyAuthenticationRequest();
}

OneginiSdk.submitFingerprintFallbackToPin = function () {
  return Platform.OS === 'ios'
    ? Promise.reject('Unfortunately this feature is not supported, yet.')
    : RNOneginiSdk.submitFingerprintFallbackToPin();
}

OneginiSdk.submitFingerprintAcceptAuthenticationRequest = function () {
  return RNOneginiSdk.submitFingerprintAcceptAuthenticationRequest();
}

OneginiSdk.submitFingerprintDenyAuthenticationRequest = function () {
  return RNOneginiSdk.submitFingerprintDenyAuthenticationRequest();
}

OneginiSdk.submitFingerprintFallbackToPin = function () {
  return RNOneginiSdk.submitFingerprintFallbackToPin();
}

OneginiSdk.startSingleSignOn = function (url) {
  return RNOneginiSdk.startSingleSignOn(url);
}

OneginiSdk.getImplicitUserDetails = function (profileId) {
  return RNOneginiSdk.getImplicitUserDetails(profileId);
}

OneginiSdk.authenticateDevice = function () {
  return RNOneginiSdk.authenticateDevice();
}

OneginiSdk.getClientResource = function () {
  return RNOneginiSdk.getClientResource();
}

export default OneginiSdk;
