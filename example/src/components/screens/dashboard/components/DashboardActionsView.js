import React from 'react';
import {StyleSheet, Text} from 'react-native';
import PropTypes from 'prop-types';
import ContentContainer from './ContentContainer';
import Button from '../../../general/Button';
import {logout, deregisterUser} from '../../../helpers/DashboardHelpers';

const renderButton = (name, onPress = () => null, disabled = true) => {
  return (
    <Button
      containerStyle={styles.button}
      name={name}
      disabled={disabled}
      onPress={onPress}
    />
  );
};

const DashboardActionsView = (props) => {
  return (
    <ContentContainer>
      <Text style={styles.helloText}>Hello, $userName!</Text>
      {renderButton('YOUR DEVICES')}
      {renderButton('MOBILE AUTH WITH OTP')}
      {renderButton('SINGLE SIGN-ON')}
      {renderButton('SETTINGS', props.onSettingsPressed, false)}
      {renderButton('LOGOUT', () => logout(props.onLogout), false)}
      {renderButton('DEREGISTER', () => deregisterUser(props.onLogout), false)}
    </ContentContainer>
  );
};

DashboardActionsView.propTypes = {
  onLogout: PropTypes.func.isRequired,
  onSettingsPressed: PropTypes.func.isRequired,
};

const styles = StyleSheet.create({
  helloText: {
    position: 'absolute',
    top: '3%',
    left: '6%',
    color: '#1e8dca',
    fontSize: 22,
    fontWeight: '400',
  },
  button: {
    marginVertical: 14,
  },
});

export default DashboardActionsView;
