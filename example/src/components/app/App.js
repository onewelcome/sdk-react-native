import React from 'react';
import { SafeAreaView, StatusBar, StyleSheet, Platform } from 'react-native';
import PinModal from '../modals/pin/PinModal';
import TwoWayOtpApiModal from '../modals/customRegistration/TwoWayOtpApiModal';
import HomeScreen from '../screens/home/HomeScreen';
import MobileAuthOTPModal from '../modals/mobileauthotp/MobileAuthOTPModal'

const App = () => {
  return (
    <>
      <StatusBar
        barStyle={Platform.OS === 'android' ? 'light-content' : 'default'}
        backgroundColor={'#4a38ae'}
      />
      <SafeAreaView style={styles.container}>
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
