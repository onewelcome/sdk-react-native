import OneginiSdk from 'react-native-sdk-beta';

const logout = async (onLogoutSuccess) => {
  try {
    await OneginiSdk.logout();
    onLogoutSuccess();
  } catch (e) {
    alert(result.errorMsg ? result.errorMsg : 'Something strange happened.');
  }
};

const deregisterUser = async (onDeregisterSuccess) => {
  const result = await OneginiSdk.getUserProfiles()
    .then((profiles) => {
      if (profiles[0] != null) {
        return OneginiSdk.deregisterUser(profiles[0].profileId);
      } else {
        throw new Error('No one user logged in.');
      }
    })
    .then(() => {
      onDeregisterSuccess();
    })
    .catch((error) => {
      alert(error);
    });
};

export {logout, deregisterUser};
