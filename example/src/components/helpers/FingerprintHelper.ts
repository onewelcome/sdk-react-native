import OnewelcomeSdk, {Types} from 'onewelcome-react-native-sdk';

const registerFingerprintAuthenticator = async (
  successful: (success: boolean) => void,
  message: (msg: string) => void,
) => {
  try {
    const profile = await OnewelcomeSdk.getAuthenticatedUserProfile();
    await OnewelcomeSdk.registerFingerprintAuthenticator(profile.profileId);
    message('Fingerprint is enabled');
    successful(true);
  } catch (error: any) {
    message(error.message);
    successful(false);
  }
};

const deregisterFingerprintAuthenticator = async (
  successful: (success: boolean) => void,
  message: (msg: string) => void,
) => {
  try {
    const profile = await OnewelcomeSdk.getAuthenticatedUserProfile();
    await OnewelcomeSdk.deregisterFingerprintAuthenticator(profile.profileId);
    message('Fingerprint is disabled');
    successful(true);
  } catch (error: any) {
    message(error.message);
    successful(false);
  }
};

const getRegisteredAuthenticators = async (
  registeredAuthenticators: (authenticators: Types.Authenticator[]) => void,
  setAllAuthenticators: (authenticators: Types.Authenticator[]) => void,
  preferred: (authenticator: Types.Authenticator) => void,
) => {
  const profile = await OnewelcomeSdk.getAuthenticatedUserProfile();
  const authenticators = await OnewelcomeSdk.getRegisteredAuthenticators(
    profile.profileId,
  );

  const allAuthenticators = await OnewelcomeSdk.getAllAuthenticators(
    profile.profileId,
  );

  console.log('allAuthenticators: ', JSON.stringify(allAuthenticators));
  console.log('authenticators: ', JSON.stringify(authenticators));

  authenticators.forEach((it) => {
    if (it.isPreferred) {
      preferred(it);
    }
  });
  registeredAuthenticators(authenticators);
  setAllAuthenticators(allAuthenticators);
};

const isFingerprintAuthenticatorRegistered = async (
  returnEnable: (enabled: boolean) => void,
) => {
  const profile = await OnewelcomeSdk.getAuthenticatedUserProfile();
  const registered = await OnewelcomeSdk.isFingerprintAuthenticatorRegistered(
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
    const profile = await OnewelcomeSdk.getAuthenticatedUserProfile();
    await OnewelcomeSdk.setPreferredAuthenticator(profile.profileId, preferred.id);
    successful(true);
    message('The ' + preferred.name + ' is set');
  } catch (error: any) {
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
