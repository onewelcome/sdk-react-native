// TODO: rename as index.ts (main index)
// TODO: test and unify errors (plain string)

import {
  NativeModules,
  Platform,
  NativeEventEmitter,
  DeviceEventEmitter,
} from 'react-native';
import * as Types from './data-types';

//

const {RNOneginiSdk} = NativeModules;

// helpers
const isIOS = Platform.OS === 'ios';
const isAndroid = Platform.OS === 'android';

//

interface SDK {
  // Setup
  startClient(sdkConfig: Types.Config): Promise<string | null>;

  // Data getters
  getIdentityProviders(): Promise<Types.IdentityProvider>;
  getAccessToken(): Promise<string>;
  getAuthenticatedUserProfile(): Promise<Types.Profile>;
  getUserProfiles(): Promise<Types.Profile[]>;
  getRedirectUri(): Promise<Types.RedirectUri>; // TODO: I think it should be moved "behind" SDK - dev should not know about it

  // Resource getters
  getImplicitDataResource(
    profileId: string,
  ): Promise<Types.ImplicitUserDetails>;
  getAppDetailsResource(): Promise<Types.AppDetailsResources>;
  getDeviceListResource(): Promise<Types.Device[]>;

  // User register/deregister
  registerUser(identityProviderId: string): Promise<Types.Profile>;
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
  setPreferredAuthenticator(
    profileId: string,
    idOneginiAuthenticator: string,
  ): Promise<any>; // TODO: check this path and check if resolve is called on Native side

  // PIN
  submitPinAction(flowString: string, action: string, pin: string | null): void;
  changePin(): Promise<any>;

}

//

export const DefaultConfig: Types.Config = {
  enableFingerprint: true,
  securityControllerClassName: 'com.rnexampleapp.SecurityController',
  enableMobileAuthenticationOtp: true,
  customProviders: [{id: '2-way-otp-api', isTwoStep: true}],
  configModelClassName: null,
};

//

const OneginiSdkTs: SDK = {
  ...(RNOneginiSdk as SDK),
  //
  // override methods if needed
  //
  startClient: (
    sdkConfig: Types.Config = DefaultConfig,
  ): Promise<string | null> => {
    console.log('START CLIENT!');

    if (isIOS) {
      return RNOneginiSdk.startClient();
    } else {
      return RNOneginiSdk.startClient(sdkConfig);
    }
  },
};

//

export default OneginiSdkTs;
