import {Alert} from 'react-native';
import OneginiSdk from 'react-native-sdk-beta';

const logout = async (onLogoutSuccess?: () => void) => {
  try {
    await OneginiSdk.logout();
    onLogoutSuccess?.();
  } catch (err) {
    Alert.alert('error', err || 'Something strange happened.');
  }
};

const deregisterUser = async (onDeregisterSuccess?: () => void) => {
  try {
    const profiles = await OneginiSdk.getUserProfiles();

    if (profiles[0]) {
      await OneginiSdk.deregisterUser(profiles[0].profileId);
      onDeregisterSuccess?.();
    } else {
      Alert.alert('error', 'Not found logged in user.');
    }
  } catch (err) {
    Alert.alert('error', err);
  }
};

export {logout, deregisterUser};
