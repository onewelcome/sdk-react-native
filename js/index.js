import { NativeModules, Platform, NativeEventEmitter, DeviceEventEmitter } from 'react-native';
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

const OneginiSdk = {
  config: {
    configModelClassName: null,
    securityControllerClassName: "com.onegini.mobile.rnexampleapp.SecurityController",
    customProviders: [{ id: '2-way-otp-api', isTwoStep: true }],
    enableMobileAuthenticationOtp: true,
    enableFingerprint: true
  },
  listeners: {
    [ONEGINI_SDK_EVENTS.ONEGINI_PIN_NOTIFICATION]: null,
    [ONEGINI_SDK_EVENTS.ONEGINI_CUSTOM_REGISTRATION_NOTIFICATION]: null,
    [ONEGINI_SDK_EVENTS.ONEGINI_MOBILE_AUTH_OTP_NOTIFICATION]: null,
    [ONEGINI_SDK_EVENTS.ONEGINI_FINGERPRINT_NOTIFICATION]: null,
  },
  addEventListener: (eventType, cb) => { // eventType = ONEGINI_SDK_EVENTS
    if (OneginiSdk.listeners[eventType]) {
      this.removeEventListener(eventType);
    }

    OneginiSdk.listeners[eventType] = OneginiEventEmitter.addListener(
      eventType,
      (item) => {
        cb(item);
      },
    );
  },
  removeEventListener: (eventType) => {
    if (OneginiSdk.listeners[eventType]) {
      OneginiSdk.listeners[eventType].remove();
      OneginiSdk.listeners[eventType] = null;
    }
  },
  startClient: (sdkConfig = OneginiSdk.config) => Platform.OS === 'ios'
    ? RNOneginiSdk.startClient()
    : RNOneginiSdk.startClient(sdkConfig),

  // Data getters
  getIdentityProviders: () => RNOneginiSdk.getIdentityProviders(),
  getAccessToken: () => RNOneginiSdk.getAccessToken(),
  getAuthenticatedUserProfile: () => RNOneginiSdk.getAuthenticatedUserProfile(),
  getUserProfiles: () => RNOneginiSdk.getUserProfiles(),
  getRedirectUri: () => RNOneginiSdk.getRedirectUri(),

  // Resource getters
  authenticateUserImplicitly: (profileId, scopes) => RNOneginiSdk.authenticateUserImplicitly(profileId, scopes),
  authenticateDeviceForResource: (scopes) => RNOneginiSdk.authenticateDeviceForResource(scopes),
  resourceRequest: (isImplicit = false, details = {}) => RNOneginiSdk.resourceRequest(isImplicit, details),

  // User register/deregister
  registerUser: (identityProvider) => RNOneginiSdk.registerUser(identityProvider), //@todo return whole user + pass userName from RN
  deregisterUser: (profileId) => RNOneginiSdk.deregisterUser(profileId),
  handleRegistrationCallback: (uri) => RNOneginiSdk.handleRegistrationCallback(uri),
  cancelRegistration: () => RNOneginiSdk.cancelRegistration(),

  // Authentication
  authenticateUser: (profileId) => RNOneginiSdk.authenticateUser(profileId),
  logout: () => RNOneginiSdk.logout(),
  getAllAuthenticators: (profileId) => RNOneginiSdk.getAllAuthenticators(profileId),
  getRegisteredAuthenticators: (profileId) => RNOneginiSdk.getRegisteredAuthenticators(profileId),
  setPreferredAuthenticator: (profileId, idOneginiAuthenticator) =>
    RNOneginiSdk.setPreferredAuthenticator(profileId, idOneginiAuthenticator),

  // PIN
  submitPinAction: (flow, action, pin = null) => RNOneginiSdk.submitPinAction(flow, action, pin),
  changePin: () => RNOneginiSdk.changePin(),

  // OTP
  enrollMobileAuthentication: () => RNOneginiSdk.enrollMobileAuthentication(),
  acceptMobileAuthConfirmation: () => RNOneginiSdk.acceptMobileAuthConfirmation(),
  denyMobileAuthConfirmation: () => RNOneginiSdk.denyMobileAuthConfirmation(),
  handleMobileAuthWithOtp: (otpCode) => RNOneginiSdk.handleMobileAuthWithOtp(otpCode),
  submitCustomRegistrationAction: (action, identityProviderId, token = null) =>
    RNOneginiSdk.submitCustomRegistrationAction(action, identityProviderId, token),

  //Fingerprint
  registerFingerprintAuthenticator: (profileId) => RNOneginiSdk.registerFingerprintAuthenticator(profileId),
  deregisterFingerprintAuthenticator: (profileId) => RNOneginiSdk.deregisterFingerprintAuthenticator(profileId),
  isFingerprintAuthenticatorRegistered: (profileId) => RNOneginiSdk.isFingerprintAuthenticatorRegistered(profileId),
  submitFingerprintAcceptAuthenticationRequest: () => Platform.OS === 'ios'
    ? Promise.reject('This method is Android only')
    : RNOneginiSdk.submitFingerprintAcceptAuthenticationRequest(),
  submitFingerprintDenyAuthenticationRequest: () => Platform.OS === 'ios'
    ? Promise.reject('This method is Android only')
    : RNOneginiSdk.submitFingerprintDenyAuthenticationRequest(),
  submitFingerprintFallbackToPin: () => Platform.OS === 'ios'
    ? Promise.reject('This method is Android only')
    : RNOneginiSdk.submitFingerprintFallbackToPin(),

  // App to Web
  startSingleSignOn: (url) => RNOneginiSdk.startSingleSignOn(url),
};
Object.freeze(OneginiSdk);

export default OneginiSdk;
