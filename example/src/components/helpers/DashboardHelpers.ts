import {Alert} from 'react-native';
import OneWelcomeSdk from 'onewelcome-react-native-sdk';

const logout = async (onLogoutSuccess?: () => void) => {
  try {
    await OneWelcomeSdk.logout();
    onLogoutSuccess?.();
  } catch (err) {
    Alert.alert('error', JSON.stringify(err) || 'Something strange happened.');
  }
};

const deregisterUser = async (onDeregisterSuccess?: () => void) => {
  try {
    const profiles = await OneWelcomeSdk.getUserProfiles();

    if (profiles[0]) {
      await OneWelcomeSdk.deregisterUser(profiles[0].profileId);
      onDeregisterSuccess?.();
    } else {
      Alert.alert('error', 'Not found logged in user.');
    }
  } catch (err) {
    Alert.alert('error', JSON.stringify(err));
  }
};

export {logout, deregisterUser};
