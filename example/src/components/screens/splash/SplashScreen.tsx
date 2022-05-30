import React, {useEffect} from 'react';
import {StyleSheet, View, Image, Alert} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import OneWelcomeSdk from 'onewelcome-react-native-sdk';
import {Assets} from '../../../../assets';

interface Props {
  onSdkStarted?: () => void;
  onSdkError?: () => void;
}

const SplashScreen: React.FC<Props> = (props) => {
  useEffect(() => {
    startSdk(props.onSdkStarted, props.onSdkError);
  }, [props.onSdkStarted]);

  return (
    <View style={styles.container}>
      <Image source={Assets.logo} />
    </View>
  );
};

const startSdk = async (onStarted?: Props['onSdkStarted'], onError?: Props['onSdkError']) => {
  try {
    await OneWelcomeSdk.startClient();

    const linkUriResult = await OneWelcomeSdk.getRedirectUri();

    await AsyncStorage.setItem(
      '@redirectUri',
      linkUriResult.redirectUri.substr(
        0,
        linkUriResult.redirectUri.indexOf(':'),
      ),
    );
    onStarted?.();
  } catch (e) {
    Alert.alert('Error when starting SDK', JSON.stringify(e));
    onError?.();
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
