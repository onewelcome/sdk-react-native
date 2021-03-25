import React, {useEffect} from 'react';
import {StyleSheet, View, Image, Alert} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import OneginiSdk from 'react-native-sdk-beta';
import {Assets} from '../../../../assets';

interface Props {
  onSdkStarted?: () => void;
}

const SplashScreen: React.FC<Props> = (props) => {
  useEffect(() => {
    startSdk(props.onSdkStarted);
  }, [props.onSdkStarted]);

  return (
    <View style={styles.container}>
      <Image source={Assets.logo} />
    </View>
  );
};

const startSdk = async (onStarted?: () => void) => {
  try {
    await OneginiSdk.startClient();

    const linkUriResult = await OneginiSdk.getRedirectUri();

    await AsyncStorage.setItem(
      '@redirectUri',
      linkUriResult.redirectUri.substr(
        0,
        linkUriResult.redirectUri.indexOf(':'),
      ),
    );
    onStarted?.();
  } catch (e) {
    Alert.alert('error', e);
  }
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
