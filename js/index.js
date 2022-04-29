import { NativeModules, Platform, NativeEventEmitter, DeviceEventEmitter } from 'react-native';
const { RNOneWelcomeSdk } = NativeModules;

const OneWelcomeEventEmitter =
  Platform.OS === 'ios'
    ? new NativeEventEmitter(RNOneWelcomeSdk)
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
    securityControllerClassName: "com.onewelcome.mobile.rnexampleapp.SecurityController",
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
    ? RNOneWelcomeSdk.startClient()
    : RNOneWelcomeSdk.startClient(sdkConfig),

  // Data getters
  getIdentityProviders: () => RNOneWelcomeSdk.getIdentityProviders(),
  getAccessToken: () => RNOneWelcomeSdk.getAccessToken(),
  getAuthenticatedUserProfile: () => RNOneWelcomeSdk.getAuthenticatedUserProfile(),
  getUserProfiles: () => RNOneWelcomeSdk.getUserProfiles(),
  getRedirectUri: () => RNOneWelcomeSdk.getRedirectUri(),

  // Resource getters
  authenticateUserImplicitly: (profileId, scopes) => RNOneWelcomeSdk.authenticateUserImplicitly(profileId, scopes),
  authenticateDeviceForResource: (scopes) => RNOneWelcomeSdk.authenticateDeviceForResource(scopes),
  resourceRequest: (isImplicit = false, details = {}) => RNOneWelcomeSdk.resourceRequest(isImplicit, details),

  // User register/deregister
  registerUser: (identityProvider) => RNOneWelcomeSdk.registerUser(identityProvider), //@todo return whole user + pass userName from RN
  deregisterUser: (profileId) => RNOneWelcomeSdk.deregisterUser(profileId),
  handleRegistrationCallback: (uri) => RNOneWelcomeSdk.handleRegistrationCallback(uri),
  cancelRegistration: () => RNOneWelcomeSdk.cancelRegistration(),

  // Authentication
  authenticateUser: (profileId) => RNOneWelcomeSdk.authenticateUser(profileId),
  logout: () => RNOneWelcomeSdk.logout(),
  getAllAuthenticators: (profileId) => RNOneWelcomeSdk.getAllAuthenticators(profileId),
  getRegisteredAuthenticators: (profileId) => RNOneWelcomeSdk.getRegisteredAuthenticators(profileId),
  setPreferredAuthenticator: (profileId, idOneWelcomeAuthenticator) =>
    RNOneWelcomeSdk.setPreferredAuthenticator(profileId, idOneWelcomeAuthenticator),

  // PIN
  submitPinAction: (flow, action, pin = null) => RNOneWelcomeSdk.submitPinAction(flow, action, pin),
  changePin: () => RNOneWelcomeSdk.changePin(),

  // OTP
  enrollMobileAuthentication: () => RNOneWelcomeSdk.enrollMobileAuthentication(),
  acceptMobileAuthConfirmation: () => RNOneWelcomeSdk.acceptMobileAuthConfirmation(),
  denyMobileAuthConfirmation: () => RNOneWelcomeSdk.denyMobileAuthConfirmation(),
  handleMobileAuthWithOtp: (otpCode) => RNOneWelcomeSdk.handleMobileAuthWithOtp(otpCode),
  submitCustomRegistrationAction: (action, identityProviderId, token = null) =>
    RNOneWelcomeSdk.submitCustomRegistrationAction(action, identityProviderId, token),

  //Fingerprint
  registerFingerprintAuthenticator: (profileId) => RNOneWelcomeSdk.registerFingerprintAuthenticator(profileId),
  deregisterFingerprintAuthenticator: (profileId) => RNOneWelcomeSdk.deregisterFingerprintAuthenticator(profileId),
  isFingerprintAuthenticatorRegistered: (profileId) => RNOneWelcomeSdk.isFingerprintAuthenticatorRegistered(profileId),
  submitFingerprintAcceptAuthenticationRequest: () => Platform.OS === 'ios'
    ? Promise.reject('This method is Android only')
    : RNOneWelcomeSdk.submitFingerprintAcceptAuthenticationRequest(),
  submitFingerprintDenyAuthenticationRequest: () => Platform.OS === 'ios'
    ? Promise.reject('This method is Android only')
    : RNOneWelcomeSdk.submitFingerprintDenyAuthenticationRequest(),
  submitFingerprintFallbackToPin: () => Platform.OS === 'ios'
    ? Promise.reject('This method is Android only')
    : RNOneWelcomeSdk.submitFingerprintFallbackToPin(),

  // App to Web
  startSingleSignOn: (url) => RNOneWelcomeSdk.startSingleSignOn(url),
};
Object.freeze(OneWelcomeSdk);

export default OneWelcomeSdk;
