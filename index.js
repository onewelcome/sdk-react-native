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
  CHANGED: 'changed'
};

export const ONEGINI_PIN_ACTIONS = {
  PROVIDE_PIN: 'provide',
  CANCEL: 'cancel',
};

export const ONEGINI_PIN_FLOW = {
  AUTHENTICATION: 'authentication',
  CREATE: 'create',
  CHANGE: 'change',
};

export const CUSTOM_REGISTRATION_NOTIFICATIONS = {
  INIT_REGISTRATION : 'initRegistration',
  FINISH_REGISTRATION: 'finishRegistration',
};

export const CUSTOM_REGISTRATION_ACTIONS = {
  PROVIDE_TOKEN: 'provide',
  CANCEL: 'cancel',
};

export const MOBILE_AUTH_OTP_NOTIFICATION = {
  START_AUTHENTICATION: 'startAuthentication',
  FINISH_AUTHENTICATION: 'finishAuthentication',
};

export const FINGERPRINT_NOTIFICATION = {
  START_AUTHENTICATION : "startAuthentication",
  ON_NEXT_AUTHENTICATION_ATTEMPT : "onNextAuthenticationAttempt",
  ON_FINGERPRINT_CAPTURED : "onFingerprintCaptured",
  FINISH_AUTHENTICATION : "finishAuthentication"
}

const OneginiSdk = {};

OneginiSdk.config = {
  configModelClassName: null,
  securityControllerClassName: "com.rnexampleapp.SecurityController",
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
  return RNOneginiSdk.getIdentityProviders();
};

OneginiSdk.getAccessToken = function () {
  return RNOneginiSdk.getAccessToken();
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
  return RNOneginiSdk.registerUser(identityProvider)
};

OneginiSdk.deregisterUser = function (profileId) {
  return RNOneginiSdk.deregisterUser(profileId);
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

//@todo implement this on Android side
OneginiSdk.submitCustomRegistrationAction = function (action, identityProviderId, token = null) {
  return RNOneginiSdk.submitCustomRegistrationAction(action, identityProviderId, token);
}

OneginiSdk.submitPinAction = function (flow, action, pin = null) {
  RNOneginiSdk.submitPinAction(flow, action, pin);
}

OneginiSdk.changePin = function () { return RNOneginiSdk.changePin(); }

OneginiSdk.authenticateUser = function (profileId) { return RNOneginiSdk.authenticateUser(profileId); }

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
  return RNOneginiSdk.logout()
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

OneginiSdk.getImplicitDataResource = function (profileId) {
  return RNOneginiSdk.getImplicitDataResource(profileId);
}

OneginiSdk.getAppDetailsResource = function () {
  return RNOneginiSdk.getAppDetailsResource();
}

OneginiSdk.getDeviceListResource = function () {
  return RNOneginiSdk.getDeviceListResource();
}

export default OneginiSdk;
