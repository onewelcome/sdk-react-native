import { NativeModules } from 'react-native';

const { RNOneginiSdk } = NativeModules;

RNOneginiSdk.startClient = function () {
  return new Promise((resolve) =>
    RNOneginiSdk.startClient((response) => resolve(response)),
  );
};

RNOneginiSdk.getIdentityProviders = function () {
  return new Promise((resolve) =>
    RNOneginiSdk.getIdentityProviders((response) => resolve(response)),
  );
};

//@todo will return profileId -> Later check out whole profile + don't forget to ask for userName on RN side
RNOneginiSdk.registerUser = function (identityProvider = null) {
  return new Promise((resolve) =>
    RNOneginiSdk.registerUser(identityProvider, (response) =>
      resolve(response),
    ),
  );
};

RNOneginiSdk.getRedirectUri = function () {
  return new Promise((resolve) =>
    RNOneginiSdk.getRedirectUri((response) => resolve(response)),
  );
};

RNOneginiSdk.handleRegistrationCallback = function (uri) {
  RNOneginiSdk.handleRegistrationCallback(uri);
}

RNOneginiSdk.cancelRegistration         = function () {
  RNOneginiSdk.cancelRegistration();
}

export default RNOneginiSdk;
