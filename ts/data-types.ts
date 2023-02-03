export interface CustomProvider {
  id: string;
  isTwoStep: boolean;
}

export interface userInfo {
  remainingFailureCount: string;
}

export interface IdentityProvider {
  id: string;
  name: string;
}

export interface Profile {
  id: string;
}

export interface ImplicitUserDetails {
  decoratedUserId: string;
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

export interface AuthData {
  userProfile: Profile;
  customInfo?: string;
}

export interface Authenticator {
  id: string;
  name: string;
  type: string;
  isPreferred: boolean;
  isRegistered: boolean;
  userProfile?: Profile;
}

export interface SingleSignOnData {
  token: string;
  url: string;
}

export enum ResourceRequestType {
  User = 'User',
  Implicit = 'ImplicitUser',
  Anonymous = 'Anonymous',
}

export interface StringMap {
  [key: string]: string;
}

export interface ResourcesDetails {
  path: string;
  method: 'GET' | 'POST' | 'PUT' | 'DELETE';
  parameters?: StringMap;
  headers?: StringMap;
}

export enum BiometricAuthenticatorIds {
  AndroidFingerprint = 'fingerprint',
  iOSBiometric = 'com.onegini.authenticator.TouchID', // for both TouchID and FaceID
}
