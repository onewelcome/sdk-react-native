import OneginiSdk from 'react-native-sdk-beta';

const logout = async (onLogoutSuccess) => {
  try {
    await OneginiSdk.logout();
    onLogoutSuccess();
  } catch (err) {
    alert(err || 'Something strange happened.');
  }
};

const deregisterUser = async (onDeregisterSuccess) => {
  try {
    const profiles = await OneginiSdk.getUserProfiles();

    if(profiles[0]) {
      await OneginiSdk.deregisterUser(profiles[0].profileId);
      onDeregisterSuccess();
    } else {
      alert('Not found logged in user.')
    }
  } catch (err) {
    alert(err);
  }
};

export {logout, deregisterUser};
