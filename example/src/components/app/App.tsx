import React, {useEffect, useState} from 'react';
import {
  SafeAreaView,
  StatusBar,
  StyleSheet,
  Platform,
  BackHandler,
  ToastAndroid,
} from 'react-native';
import PinModal from '../modals/pin/PinModal';
import TwoWayOtpApiModal from '../modals/customRegistration/TwoWayOtpApiModal';
import HomeScreen from '../screens/home/HomeScreen';
import MobileAuthOTPModal from '../modals/mobileauthotp/MobileAuthOTPModal';
import FingerprintModal from '../modals/fingerprint/FingerprintModal';

const App: React.FC<{}> = () => {
  const [isReadyToExit, setIsReadyToExit] = useState(false);

  useEffect(() => {
    const subscriber = BackHandler.addEventListener('hardwareBackPress', () => {
      if (isReadyToExit) {
        return false;
      }

      setIsReadyToExit(true);

      setTimeout(() => setIsReadyToExit(false), 2000);

      ToastAndroid.show('Click back again to exit.', ToastAndroid.SHORT);

      return true;
    });

    return () => subscriber.remove();
  }, [isReadyToExit]);

  return (
    <>
      <StatusBar
        barStyle={Platform.OS === 'android' ? 'light-content' : 'default'}
        backgroundColor={'#4a38ae'}
      />
      <SafeAreaView style={styles.container}>
        <FingerprintModal />
        <MobileAuthOTPModal />
        <TwoWayOtpApiModal />
        <PinModal />
        <HomeScreen />
      </SafeAreaView>
    </>
  );
};

const styles = StyleSheet.create({
  container: {
    width: '100%',
    height: '100%',
    backgroundColor: '#ffffff',
  },
});

export default App;
