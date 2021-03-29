import OneginiSdk, {Types} from 'react-native-sdk-beta';

const registerFingerprintAuthenticator = async (
  successful: (success: boolean) => void,
  message: (msg: string) => void,
) => {
  try {
    const profile = await OneginiSdk.getAuthenticatedUserProfile();
    await OneginiSdk.registerFingerprintAuthenticator(profile.profileId);
    message('Fingerprint is enabled');
    successful(true);
  } catch (error) {
    message(error.message);
    successful(false);
  }
};

const deregisterFingerprintAuthenticator = async (
  successful: (success: boolean) => void,
  message: (msg: string) => void,
) => {
  try {
    const profile = await OneginiSdk.getAuthenticatedUserProfile();
    await OneginiSdk.deregisterFingerprintAuthenticator(profile.profileId);
    message('Fingerprint is disabled');
    successful(true);
  } catch (error) {
    message(error.message);
    successful(false);
  }
};

const getRegisteredAuthenticators = async (
  registeredAuthenticators: (authenticators: Types.Authenticator[]) => void,
  preferred: (authenticator: Types.Authenticator) => void,
) => {
  const profile = await OneginiSdk.getAuthenticatedUserProfile();
  const authenticators = await OneginiSdk.getRegisteredAuthenticators(
    profile.profileId,
  );
  console.log('authenticators: ', JSON.stringify(authenticators));
  authenticators.forEach((it) => {
    if (it.isPreferred) {
      preferred(it);
    }
  });
  registeredAuthenticators(authenticators);
};

const isFingerprintAuthenticatorRegistered = async (
  returnEnable: (enabled: boolean) => void,
) => {
  const profile = await OneginiSdk.getAuthenticatedUserProfile();
  const registered = await OneginiSdk.isFingerprintAuthenticatorRegistered(
    profile.profileId,
  );
  returnEnable(registered);
};

const setPreferredAuthenticator = async (
  preferred: Types.Authenticator,
  successful: (success: boolean) => void,
  message: (msg: string) => void,
) => {
  console.log('preferred');
  console.log(preferred);
  try {
    const profile = await OneginiSdk.getAuthenticatedUserProfile();
    await OneginiSdk.setPreferredAuthenticator(profile.profileId, preferred.id);
    successful(true);
    message('The ' + preferred.name + ' is set');
  } catch (error) {
    message(error.message);
    successful(false);
  }
};

export {
  registerFingerprintAuthenticator,
  deregisterFingerprintAuthenticator,
  isFingerprintAuthenticatorRegistered,
  getRegisteredAuthenticators,
  setPreferredAuthenticator,
};
