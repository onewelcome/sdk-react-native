// TODO: rename as index.ts (main index)
// TODO: separate modules? Start, Resources, ...
// TODO: test and unify errors (plain string)

import {
  NativeModules,
  Platform,
  NativeEventEmitter,
  DeviceEventEmitter,
} from 'react-native';
const {RNOneginiSdk} = NativeModules;

// helpers
const isIOS = Platform.OS === 'ios';
const isAndroid = Platform.OS === 'android';

//

namespace OneginiSdkTs {
  //
  // Start
  //

  export interface CustomProvider {
    id: string;
    isTwoStep: boolean;
  }

  export interface Config {
    enableFingerprint: boolean;
    securityControllerClassName: string;
    enableMobileAuthenticationOtp: boolean;
    configModelClassName: string | null;
    customProviders: [CustomProvider?];
  }

  const DefaultConfig: Config = {
    enableFingerprint: true,
    securityControllerClassName: 'com.rnexampleapp.SecurityController',
    enableMobileAuthenticationOtp: true,
    customProviders: [{id: '2-way-otp-api', isTwoStep: true}],
    configModelClassName: null,
  };

  // Promise returns error msg?
  export function startClient(
    sdkConfig: Config = DefaultConfig,
  ): Promise<string | null> {
    return isIOS
      ? RNOneginiSdk.startClient() // why there is no default config on iOS?
      : RNOneginiSdk.startClient(sdkConfig);
  }

  //
  // Resources
  //

  export interface IdentityProvider {
    id: string;
    name: string;
  }

  export interface Profile {
    profileId: string;
  }

  export interface ImplicitUserDetails {
    decoratedUserId: string;
  }

  export interface RedirectUri {
    redirectUri: string;
  }

  export interface AppDetailsResources {
    applicationIdentifier: string;
    applicationPlatform: string;
    applicationVersion: string;
  }

  export interface Device {
    id: string;
    name: string;
    application: string;
    platform: string;
    isMobileAuthenticationEnabled: boolean;
  }

  //

  export function getIdentityProviders(): Promise<IdentityProvider> {
    return RNOneginiSdk.getIdentityProviders();
  }

  // TODO: use it in ExampleApp
  // Does it return string or object map?
  export function getAccessToken(): Promise<string> {
    return RNOneginiSdk.getAccessToken();
  }

  // TODO: use it in ExampleApp
  export function getAuthenticatedUserProfile(): Promise<Profile> {
    return RNOneginiSdk.getAuthenticatedUserProfile();
  }

  export function getUserProfiles(): Promise<Profile[]> {
    return RNOneginiSdk.getUserProfiles();
  }

  // TODO: I think it should be moved "behind" SDK - dev should not know about it
  export function getRedirectUri(): Promise<RedirectUri> {
    return RNOneginiSdk.getRedirectUri();
  }

  export function getImplicitDataResource(
    profileId: string,
  ): Promise<ImplicitUserDetails> {
    return RNOneginiSdk.getImplicitDataResource(profileId);
  }

  export function getAppDetailsResource(): Promise<AppDetailsResources> {
    return RNOneginiSdk.getAppDetailsResource();
  }

  export function getDeviceListResource(): Promise<Device[]> {
    return RNOneginiSdk.getDeviceListResource();
  }

  //
  // User register/deregister
  //

  export function registerUser(identityProviderId: string): Promise<Profile> {
    //@todo return whole user + pass userName from RN
    return RNOneginiSdk.registerUser(identityProviderId);
  }

  export function deregisterUser(profileId: string): Promise<any> {
    return RNOneginiSdk.deregisterUser(profileId);
  }

  // TODO: I think it should be moved "behind" SDK - dev should not know about it
  export function handleRegistrationCallback(uri: string) {
    RNOneginiSdk.handleRegistrationCallback(uri);
  }

  export function cancelRegistration() {
    RNOneginiSdk.cancelRegistration();
  }

  //
  //
  //
}

export default OneginiSdkTs;
