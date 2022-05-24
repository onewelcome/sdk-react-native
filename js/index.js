import { NativeModules, Platform, NativeEventEmitter, DeviceEventEmitter } from 'react-native';
const { RNOneginiSdk } = NativeModules;

const OneWelcomeEventEmitter =
  Platform.OS === 'ios'
    ? new NativeEventEmitter(RNOneginiSdk)
    : DeviceEventEmitter;

export const ONEWELCOME_SDK_EVENTS = {
  ONEWELCOME_PIN_NOTIFICATION: 'ONEWELCOME_PIN_NOTIFICATION',
  ONEWELCOME_CUSTOM_REGISTRATION_NOTIFICATION: 'ONEWELCOME_CUSTOM_REGISTRATION_NOTIFICATION',
  ONEWELCOME_MOBILE_AUTH_OTP_NOTIFICATION: 'ONEWELCOME_MOBILE_AUTH_OTP_NOTIFICATION',
  ONEWELCOME_FINGERPRINT_NOTIFICATION: 'ONEWELCOME_FINGERPRINT_NOTIFICATION'
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

const OneWelcomeSdk = {
  config: {
    configModelClassName: null,
    securityControllerClassName: "com.onegini.mobile.rnexampleapp.SecurityController",
    customProviders: [{ id: '2-way-otp-api', isTwoStep: true }],
    enableMobileAuthenticationOtp: true,
    enableFingerprint: true
  },
  listeners: {
    [ONEWELCOME_SDK_EVENTS.ONEWELCOME_PIN_NOTIFICATION]: null,
    [ONEWELCOME_SDK_EVENTS.ONEWELCOME_CUSTOM_REGISTRATION_NOTIFICATION]: null,
    [ONEWELCOME_SDK_EVENTS.ONEWELCOME_MOBILE_AUTH_OTP_NOTIFICATION]: null,
    [ONEWELCOME_SDK_EVENTS.ONEWELCOME_FINGERPRINT_NOTIFICATION]: null,
  },
  addEventListener: (eventType, cb) => { // eventType = ONEWELCOME_SDK_EVENTS
    if (OneWelcomeSdk.listeners[eventType]) {
      this.removeEventListener(eventType);
    }

    OneWelcomeSdk.listeners[eventType] = OneWelcomeEventEmitter.addListener(
      eventType,
      (item) => {
        cb(item);
      },
    );
  },
  removeEventListener: (eventType) => {
    if (OneWelcomeSdk.listeners[eventType]) {
      OneWelcomeSdk.listeners[eventType].remove();
      OneWelcomeSdk.listeners[eventType] = null;
    }
  },
  startClient: (sdkConfig = OneWelcomeSdk.config) => Platform.OS === 'ios'
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
Object.freeze(OneWelcomeSdk);

export default OneWelcomeSdk;
