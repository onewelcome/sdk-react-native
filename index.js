import { NativeModules } from 'react-native';

const { RNOneginiSdk } = NativeModules;

const OneginiSdk = {};

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


export default OneginiSdk;
