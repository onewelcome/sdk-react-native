import React, {useEffect} from 'react';
import {StyleSheet, View, Image} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import OneginiSdk from 'react-native-sdk-beta';
import {Assets} from '../../../../assets';

const startSdk = async (onStarted = () => null) => {
  const startResult = await OneginiSdk.startClient(OneginiSdk.config);

  if (startResult.success) {
    const linkUriResult = await OneginiSdk.getRedirectUri();

    if (linkUriResult.success) {
      await AsyncStorage.setItem(
        '@redirectUri',
        linkUriResult.redirectUri.substr(
          0,
          linkUriResult.redirectUri.indexOf(':'),
        ),
      );
      onStarted();
    } else {
      alert(
        linkUriResult.errorMsg
          ? linkUriResult.errorMsg
          : 'Failed to get redirect URL.',
      );
    }
  } else {
    alert(
      startResult.errorMsg
        ? startResult.errorMsg
        : 'Something strange happened.',
    );
  }
};

const SplashScreen = (props) => {
  useEffect(() => {
    startSdk(props.onSdkStarted);
  }, [props.onSdkStarted]);

  return (
    <View style={styles.container}>
      <Image source={Assets.logo} />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  logo: {
    width: '100%',
    resizeMode: 'contain',
  },
});

export default SplashScreen;
