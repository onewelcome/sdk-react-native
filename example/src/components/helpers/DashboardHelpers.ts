import {Alert} from 'react-native';
import OnewelcomeSdk from 'onewelcome-react-native-sdk';

const logout = async (onLogoutSuccess?: () => void) => {
  try {
    await OnewelcomeSdk.logout();
    onLogoutSuccess?.();
  } catch (err) {
    Alert.alert('error', JSON.stringify(err) || 'Something strange happened.');
  }
};

const deregisterUser = async (onDeregisterSuccess?: () => void) => {
  try {
    const profiles = await OnewelcomeSdk.getUserProfiles();

    if (profiles[0]) {
      await OnewelcomeSdk.deregisterUser(profiles[0].profileId);
      onDeregisterSuccess?.();
    } else {
      Alert.alert('error', 'Not found logged in user.');
    }
  } catch (err) {
    Alert.alert('error', JSON.stringify(err));
  }
};

export {logout, deregisterUser};
