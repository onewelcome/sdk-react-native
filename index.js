import { NativeModules, Platform, NativeEventEmitter, DeviceEventEmitter } from 'react-native';

const { RNOneginiSdk } = NativeModules;

const OneginiEventEmitter =
        Platform.OS === 'ios'
          ? new NativeEventEmitter(RNOneginiSdk)
          : DeviceEventEmitter;

export const ONEGINI_SDK_EVENTS = {
  ONEGINI_PIN_NOTIFICATION: 'ONEGINI_PIN_NOTIFICATION',
};

export const ONEGINI_PIN_NOTIFICATIONS = {
  OPEN: 'open',
  CONFIRM: 'confirm',
  CLOSE: 'close',
  ERROR: 'show_error',
  AUTH_ATTEMPT: 'auth_attempt',
};

export const ONEGINI_PIN_ACTIONS = {
  PROVIDE_PIN: 'provide',
  CANCEL: 'cancel',
};

const OneginiSdk = {};


OneginiSdk.listeners = {
  [ONEGINI_SDK_EVENTS.ONEGINI_PIN_NOTIFICATION]: null, // fires ONEGINI_PIN_NOTIFICATIONS
};

// eventType = ONEGINI_SDK_EVENTS
OneginiSdk.addEventListener = function (eventType, cb) {
  if (this.listeners[eventType]) {
    this.removeEventListener(eventType);
  }

  this.listeners[eventType] = OneginiEventEmitter.addListener(
    eventType,
    (item) => cb(item),
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
    RNOneginiSdk.startClient((response) => resolve(response)),
  );
};

OneginiSdk.getIdentityProviders = function () {
  return new Promise((resolve) =>
    RNOneginiSdk.getIdentityProviders((response) => resolve(response)),
  );
};

//@todo will return profileId -> Later check out whole profile + don't forget to ask for userName on RN side
OneginiSdk.registerUser = function (identityProvider = null) {
  return new Promise((resolve) =>
    RNOneginiSdk.registerUser(identityProvider, (response) =>
      resolve(response),
    ),
  );
};

OneginiSdk.getRedirectUri = function () {
  return new Promise((resolve) =>
    RNOneginiSdk.getRedirectUri((response) => resolve(response)),
  );
};

OneginiSdk.handleRegistrationCallback = function (uri) {
  RNOneginiSdk.handleRegistrationCallback(uri);
}

OneginiSdk.cancelRegistration         = function () {
  RNOneginiSdk.cancelRegistration();
}

OneginiSdk.setConfigModelClassName = function (className = null) {
  RNOneginiSdk.setConfigModelClassName(className);
}

OneginiSdk.setSecurityControllerClassName = function (className = null) {
  RNOneginiSdk.setSecurityControllerClassName(className);
}

// action = ONEGINI_PIN_ACTIONS
OneginiSdk.submitPinAction = function (action, isCreatePinFlow = false, pin = null) {
  RNOneginiSdk.submitPinAction(action, isCreatePinFlow, pin);
}


export default OneginiSdk;
