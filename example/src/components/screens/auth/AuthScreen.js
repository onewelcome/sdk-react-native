import React from 'react';
import {Image, StyleSheet, View, Text} from 'react-native';
import PropTypes from 'prop-types';
import {Assets} from '../../../../assets';
import RegisterButton from './components/RegisterButton';
import AuthButton from './components/AuthButton';

const AuthScreen = (props) => {
  return basView();

  function basView() {
    return (
      <View style={styles.container}>
        <View style={styles.logoHolder}>
          <Image source={Assets.logo} />
          <Text style={styles.logoText}>Example App</Text>
        </View>
        <AuthButton onAuthorized={props.onAuthorized} />
        <View style={styles.registerContainer}>
          <RegisterButton onRegistered={props.onAuthorized} />
        </View>
      </View>
    );
  }
};

AuthScreen.propTypes = {
  onAuthorized: PropTypes.func.isRequired,
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingTop: '10%',
    paddingHorizontal: '15%',
  },
  logoHolder: {
    width: '100%',
    height: '15%',
    alignItems: 'center',
  },
  logo: {
    width: '100%',
    height: '100%',
    resizeMode: 'contain',
  },
  logoText: {
    position: 'absolute',
    bottom: '20%',
    right: '8%',
    color: '#777777',
    fontSize: 20,
    fontWeight: '400',
  },
  registerContainer: {
    position: 'absolute',
    bottom: '8%',
    width: '100%',
    alignSelf: 'center',
  },
});

export default AuthScreen;
