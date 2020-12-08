import OneginiSdk from 'react-native-sdk-beta';

const registerFingerprintAuthenticator = async (successful, message) => {
  try {
    const profile = await OneginiSdk.getAuthenticatedUserProfile();
    await OneginiSdk.registerFingerprintAuthenticator(profile.profileId)
    message("Fingerprint is enabled")
    successful(true)
  } catch (error) {
    message(e.message)
    successful(false)
  }
};

const deregisterFingerprintAuthenticator = async (successful, message) => {
  try {
    const profile = await OneginiSdk.getAuthenticatedUserProfile();
    await OneginiSdk.deregisterFingerprintAuthenticator(profile.profileId)
    message("Fingerprint is disabled")
    successful(true)
  } catch (error) {
    message(e.message)
    successful(false)
  }
};

const isFingerprintAuthenticatorRegistered = async (returnEnable) => {
  const profile = await OneginiSdk.getAuthenticatedUserProfile();
  const registered = await OneginiSdk.isFingerprintAuthenticatorRegistered(profile.profileId);
  returnEnable(registered)
};

export { registerFingerprintAuthenticator, deregisterFingerprintAuthenticator, isFingerprintAuthenticatorRegistered };
