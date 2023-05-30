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
  customInfo?: CustomInfo;
}

export interface CustomInfo {
  status: number;
  data: string;
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

export type ResourcesDetails =
  | ResourcesDetailsGet
  | ResourceDetailsPost
  | ResourceDetailsDelete
  | ResourceDetailsPut;

export interface ResourcesDetailsGet {
  path: string;
  method: ResourceMethod.GET;
  headers?: StringMap;
}

export interface ResourceDetailsDelete {
  path: string;
  method: ResourceMethod.DELETE;
  headers?: StringMap;
  body?: string;
}

export interface ResourceDetailsPost {
  path: string;
  method: ResourceMethod.POST;
  headers?: StringMap;
  body: string;
}

export interface ResourceDetailsPut {
  path: string;
  method: ResourceMethod.PUT;
  headers?: StringMap;
  body: string;
}

export enum ResourceMethod {
  GET = 'GET',
  POST = 'POST',
  DELETE = 'DELETE',
  PUT = 'PUT',
}
export interface ResourceResponse {
  body: string;
  headers: StringMap;
  ok: boolean;
  status: number;
}

export enum BiometricAuthenticatorIds {
  AndroidFingerprint = 'fingerprint',
  iOSBiometric = 'com.onegini.authenticator.TouchID', // for both TouchID and FaceID
}
