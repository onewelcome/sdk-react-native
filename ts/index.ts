import {
  NativeModules,
  Platform,
  NativeEventEmitter,
  DeviceEventEmitter,
  EmitterSubscription,
} from 'react-native';
import * as Types from './data-types';
import * as Events from './events';
import SDKError from './errors';

//

const {RNOneginiSdk} = NativeModules;

const OneWelcomeEventEmitter =
  Platform.OS === 'ios'
    ? new NativeEventEmitter(RNOneginiSdk)
    : DeviceEventEmitter;

// helpers
const isIOS = () => Platform.OS === 'ios';
const isAndroid = () => Platform.OS === 'android';

//

interface NativeMethods {
  // listeners
  addEventListener(
    eventType: Events.SdkNotification,
    callback?: (event: Events.SdkEvent) => void,
  ): EmitterSubscription;

  addEventListener(
    eventType: Events.SdkNotification.PinCreate,
    callback?: (event: Events.PinCreateEvent) => void,
  ): EmitterSubscription;

  addEventListener(
    eventType: Events.SdkNotification.PinAuth,
    callback?: (event: Events.PinAuthenticationEvent) => void,
  ): EmitterSubscription;

  addEventListener(
    eventType: Events.SdkNotification.CustomRegistration,
    callback?: (event: Events.CustomRegistrationEvent) => void,
  ): EmitterSubscription;

  addEventListener(
    eventType: Events.SdkNotification.MobileAuthOtp,
    callback?: (event: Events.MobileAuthOtpEvent) => void,
  ): EmitterSubscription;

  addEventListener(
    eventType: Events.SdkNotification.Fingerprint,
    callback?: (event: Events.FingerprintEvent) => void,
  ): EmitterSubscription;

  addEventListener(
    eventType: Events.SdkNotification.Registration,
    callback?: (event: Events.RegistrationURLEvent) => void,
  ): EmitterSubscription;

  // Setup
  startClient(sdkConfig?: Types.Config): Promise<string | null>;

  // Data getters
  getIdentityProviders(): Promise<Types.IdentityProvider[]>;
  getAccessToken(): Promise<string>;
  getAuthenticatedUserProfile(): Promise<Types.Profile>;
  getUserProfiles(): Promise<Types.Profile[]>;
  getRedirectUri(): Promise<string>;

  // Resource getters
  //@todo extend types for details and responses
  authenticateUserImplicitly(
    profileId: string,
    scopes?: string[],
  ): Promise<void>;
  authenticateDeviceForResource(scopes?: string[]): Promise<void>;
  resourceRequest(
    type: Types.ResourceRequestType,
    details: Types.ResourcesDetails,
  ): Promise<any>;

  // User register/deregister
  registerUser(
    identityProviderId: string | null,
    scopes?: String[],
  ): Promise<Types.Profile>;
  deregisterUser(profileId: string): Promise<void>;
  deregisterAuthenticator(authenticatorId: string): Promise<void>;
  handleRegistrationCallback(uri: string): Promise<void>;
  cancelBrowserRegistration(): Promise<void>;
  cancelCustomRegistration(message: string): Promise<void>;

  // Authentication
  authenticateUser(
    profileId: string,
    authenticatorId: string | null,
  ): Promise<Types.AuthData>;
  logout(): Promise<void>;
  getAllAuthenticators(profileId: string): Promise<Types.Authenticator[]>; // TODO: use it in ExampleApp
  getRegisteredAuthenticators(
    profileId: string,
  ): Promise<Types.Authenticator[]>;
  getAllAuthenticators(profileId: string): Promise<Types.Authenticator[]>;
  setPreferredAuthenticator(
    profileId: string,
    authenticatorId: string,
  ): Promise<void>;
  validatePinWithPolicy(pin: string): Promise<void>;
  registerAuthenticator(authenticatorId: string): Promise<void>;

  // PIN
  submitPin(flow: Events.PinFlow, pin: string): Promise<void>;
  changePin(): Promise<void>;
  cancelPinAuthentication(): Promise<void>;
  cancelPinCreation(): Promise<void>;

  // OTP
  enrollMobileAuthentication(): Promise<void>;
  acceptMobileAuthConfirmation(): Promise<void>;
  denyMobileAuthConfirmation(): Promise<void>;
  handleMobileAuthWithOtp(otpCode: string): Promise<void>;
  submitCustomRegistrationAction(
    identityProviderId: string,
    token: string | null,
  ): Promise<void>;

  // Fingerprint
  registerFingerprintAuthenticator(profileId: string): Promise<any>;
  deregisterFingerprintAuthenticator(profileId: string): Promise<void>;
  submitFingerprintAcceptAuthenticationRequest(): Promise<any>;
  submitFingerprintDenyAuthenticationRequest(): Promise<any>;
  submitFingerprintFallbackToPin(): Promise<any>;

  // App to Web
  startSingleSignOn(uri: string): Promise<Types.SingleSignOnData>;
}

//
const DefaultConfig: Types.Config = {
  enableFingerprint: true,
  securityControllerClassName:
    'com.onegini.mobile.rnexampleapp.SecurityController',
  enableMobileAuthenticationOtp: true,
  customProviders: [{id: '2-way-otp-api', isTwoStep: true}],
  configModelClassName: null,
};

//

const nativeMethods: NativeMethods = {
  ...(RNOneginiSdk as NativeMethods),

  //
  // Listeners
  //

  addEventListener: (
    eventType: string,
    callback: (event: Events.SdkEvent) => void,
  ): EmitterSubscription => {
    return OneWelcomeEventEmitter.addListener(eventType, callback);
  },

  //
  // override methods if needed
  //

  startClient: (
    sdkConfig: Types.Config = DefaultConfig,
  ): Promise<string | null> => {
    if (isIOS()) {
      return RNOneginiSdk.startClient();
    }

    return RNOneginiSdk.startClient(sdkConfig);
  },

  authenticateUserImplicitly: (
    profileId: string,
    scopes?: string[],
  ): Promise<void> => {
    return scopes
      ? RNOneginiSdk.authenticateUserImplicitly(profileId, scopes)
      : RNOneginiSdk.authenticateUserImplicitly(profileId, []);
  },

  authenticateDeviceForResource: (scopes?: string[]): Promise<void> => {
    return scopes
      ? RNOneginiSdk.authenticateDeviceForResource(scopes)
      : RNOneginiSdk.authenticateDeviceForResource([]);
  },

  registerUser: (
    identityProviderId: string | null,
    scopes?: String[],
  ): Promise<Types.Profile> => {
    return scopes
      ? RNOneginiSdk.registerUser(identityProviderId, scopes)
      : RNOneginiSdk.registerUser(identityProviderId, []);
  },

  submitFingerprintAcceptAuthenticationRequest: (): Promise<any> => {
    if (isIOS()) {
      return Promise.reject('This method is Android only');
    }
    return RNOneginiSdk.submitFingerprintAcceptAuthenticationRequest();
  },

  submitFingerprintDenyAuthenticationRequest: (): Promise<any> => {
    if (isIOS()) {
      return Promise.reject('This method is Android only');
    }
    return RNOneginiSdk.submitFingerprintDenyAuthenticationRequest();
  },

  submitFingerprintFallbackToPin: (): Promise<any> => {
    if (isIOS()) {
      return Promise.reject('This method is Android only');
    }
    return RNOneginiSdk.submitFingerprintFallbackToPin();
  },
  //
  resourceRequest: (
    type: Types.ResourceRequestType,
    details: Types.ResourcesDetails,
  ): Promise<any> => {
    return new Promise((resolve, reject) => {
      RNOneginiSdk.resourceRequest(type, details)
        .then(
          (results: string) =>
            isAndroid() ? resolve(JSON.parse(results)) : resolve(results), // on Android we send string - we need to parse it
        )
        .catch(reject);
    });
  },
};

//

const OneginiSdk = {
  ...nativeMethods,
};

export {Events, Types, DefaultConfig, SDKError};

export default OneginiSdk;
