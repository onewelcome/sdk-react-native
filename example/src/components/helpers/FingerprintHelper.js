import OneginiSdk from 'react-native-sdk-beta';
import OneginiSdkTs from "react-native-sdk-beta/ts/index_ts";

const registerFingerprintAuthenticator = async (successful, message) => {
  try {
    const profile = await OneginiSdkTs.getAuthenticatedUserProfile();
    await OneginiSdk.registerFingerprintAuthenticator(profile.profileId)
    message("Fingerprint is enabled")
    successful(true)
  } catch (error) {
    message(error.message)
    successful(false)
  }
};

const deregisterFingerprintAuthenticator = async (successful, message) => {
  try {
    const profile = await OneginiSdkTs.getAuthenticatedUserProfile();
    await OneginiSdk.deregisterFingerprintAuthenticator(profile.profileId)
    message("Fingerprint is disabled")
    successful(true)
  } catch (error) {
    message(error.message)
    successful(false)
  }
};

const getRegisteredAuthenticators = async (registeredAuthenticators, preferred) => {
  const profile = await OneginiSdkTs.getAuthenticatedUserProfile();
  const authenticators = await OneginiSdkTs.getRegisteredAuthenticators(profile.profileId);
  console.log("authenticators: ", JSON.stringify(authenticators));
  authenticators.forEach(it => {
    if (it.isPreferred) {
      preferred(it)
    }
  });
  registeredAuthenticators(authenticators)
}

const isFingerprintAuthenticatorRegistered = async (returnEnable) => {
  const profile = await OneginiSdkTs.getAuthenticatedUserProfile();
  const registered = await OneginiSdk.isFingerprintAuthenticatorRegistered(profile.profileId);
  returnEnable(registered)
};

const setPreferredAuthenticator = async (preferred, successful, message) => {
  console.log("preferred")
  console.log(preferred)
  try {
    const profile = await OneginiSdkTs.getAuthenticatedUserProfile();
    await OneginiSdkTs.setPreferredAuthenticator(profile.profileId, preferred.id);
    successful(true)
    message("The " + preferred.name + " is set")
  } catch (error) {
    message(error.message)
    successful(false)
  }
};

export { registerFingerprintAuthenticator, deregisterFingerprintAuthenticator, isFingerprintAuthenticatorRegistered, getRegisteredAuthenticators, setPreferredAuthenticator };
