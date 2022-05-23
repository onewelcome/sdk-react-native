import React from 'react';
import {StyleSheet, Text, Linking, Alert} from 'react-native';
import ContentContainer from './ContentContainer';
import Button from '../../../general/Button';
import {logout, deregisterUser} from '../../../helpers/DashboardHelpers';
import OneWelcomeSdk from 'onewelcome-react-native-sdk';
import {CurrentUser} from '../../../../auth/auth';

const onSingleSingOn = () => {
  OneWelcomeSdk.startSingleSignOn(
    'https://login-mobile.test.onegini.com/personal/dashboard',
  )
    .then((it) => {
      Linking.openURL(it.url);
    })
    .catch((error) => {
      Alert.alert('Error', JSON.stringify(error));
    });
};

const renderButton = (
  name: string,
  onPress?: () => void,
  disabled: boolean = true,
) => {
  return (
    <Button
      containerStyle={styles.button}
      name={name}
      disabled={disabled}
      onPress={onPress}
    />
  );
};

interface Props {
  onLogout?: () => void;
  onSettingsPressed?: () => void;
  onMobileAuthWithOTPPressed?: () => void;
  onYourDevicesPressed?: () => void;
  onAccessTokenPressed?: () => void;
}

const DashboardActionsView: React.FC<Props> = (props) => {
  return (
    <ContentContainer>
      <Text style={styles.helloText}>{`Hello user: ${CurrentUser.id}`}</Text>
      {renderButton('YOUR DEVICES', props.onYourDevicesPressed, false)}
      {renderButton(
        'MOBILE AUTH WITH OTP',
        props.onMobileAuthWithOTPPressed,
        false,
      )}
      {renderButton('SINGLE SIGN-ON', () => onSingleSingOn(), false)}
      {renderButton('SETTINGS', props.onSettingsPressed, false)}
      {renderButton('DEREGISTER', () => deregisterUser(props.onLogout), false)}
      {renderButton('LOGOUT', () => logout(props.onLogout), false)}
      {renderButton(
        'ACCESS TOKEN',
        () => props.onAccessTokenPressed?.(),
        false,
      )}
    </ContentContainer>
  );
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
    flex:1,
  },
});

export default DashboardActionsView;
