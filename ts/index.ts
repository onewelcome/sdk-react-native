import {
  NativeModules,
  Platform,
  NativeEventEmitter,
  DeviceEventEmitter,
  EmitterSubscription,
} from 'react-native';
import * as Types from './data-types';
import * as Events from './events';
import {usePinFlow} from './pin-flow';
import {useFingerprintFlow} from './fingerprint-flow';
import {useResources} from './resource';

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
    eventType: string,
    callback?: (event: any) => void,
  ): EmitterSubscription;

  // Setup
  startClient(sdkConfig?: Types.Config): Promise<string | null>;

  // Data getters
  getIdentityProviders(): Promise<Types.IdentityProvider[]>;
  getAccessToken(): Promise<string>;
  getAuthenticatedUserProfile(): Promise<Types.Profile>;
  getUserProfiles(): Promise<Types.Profile[]>;
  getRedirectUri(): Promise<Types.RedirectUri>; // TODO: I think it should be moved "behind" SDK - dev should not know about it

  // Resource getters
  //@todo extend types for details and responses
  authenticateUserImplicitly(profileId: string, scopes?: string[]): Promise<any>;
  authenticateDeviceForResource(scopes?: string[]): Promise<any>;
  resourceRequest(
    type: Types.ResourceRequestType,
    details: Types.ResourcesDetails,
  ): Promise<any>;

  // User register/deregister
  registerUser(
    identityProviderId: string | null,
    scopes: String[],
  ): Promise<Types.Profile>;
  deregisterUser(profileId: string): Promise<any>;
  handleRegistrationCallback(uri: string): void; // TODO: I think it should be moved "behind" SDK - dev should not know about it
  cancelRegistration(): void;

  // Authentication
  authenticateUser(profileId: string): Promise<Types.AuthData>;
  logout(): Promise<any>; // any or void when we have null from native?
  getAllAuthenticators(profileId: string): Promise<Types.Authenticator[]>; // TODO: use it in ExampleApp
  getRegisteredAuthenticators(
    profileId: string,
  ): Promise<Types.Authenticator[]>;
  getAllAuthenticators(profileId: string): Promise<Types.Authenticator[]>;
  setPreferredAuthenticator(
    profileId: string,
    idOneginiAuthenticator: string,
  ): Promise<any>; // TODO: check this path and check if resolve is called on Native side

  // PIN
  submitPinAction(
    flow: Events.PinFlow,
    action: Events.PinAction,
    pin: string | null,
  ): void;
  changePin(): Promise<any>;

  // OTP
  enrollMobileAuthentication(): Promise<any>;
  acceptMobileAuthConfirmation(): Promise<any>;
  denyMobileAuthConfirmation(): Promise<any>;
  handleMobileAuthWithOtp(otpCode: string): Promise<any>;
  submitCustomRegistrationAction(
    customAction: string,
    identityProviderId: string,
    token: string | null,
  ): void;

  // Fingerprint
  registerFingerprintAuthenticator(profileId: string): Promise<any>;
  deregisterFingerprintAuthenticator(profileId: string): Promise<any>;
  isFingerprintAuthenticatorRegistered(profileId: string): Promise<boolean>;
  submitFingerprintAcceptAuthenticationRequest(): Promise<any>;
  submitFingerprintDenyAuthenticationRequest(): Promise<any>;
  submitFingerprintFallbackToPin(): Promise<any>;

  // App to Web
  startSingleSignOn(url: string): Promise<Types.SingleSignOnData>;
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
    callback: (event: any) => void,
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

export {
  Events,
  Types,
  usePinFlow,
  useFingerprintFlow,
  useResources,
  DefaultConfig,
};

export default OneginiSdk;
