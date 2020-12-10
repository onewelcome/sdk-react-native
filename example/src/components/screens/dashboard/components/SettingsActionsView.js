import React, {useState} from 'react';
import {Text, StyleSheet} from 'react-native';
import PropTypes from 'prop-types';
import OneginiSdk from 'react-native-sdk-beta';
import ContentContainer from './ContentContainer';
import Button from '../../../general/Button';
import { enrollMobileAuthentication } from '../../../helpers/MobileAuthenticationHelper';

const onChangePinPressed = () => {
  //@todo handle deregistration error when codes will be presented
  OneginiSdk.changePin()
    .then(() => alert('Success'))
    .catch((error) => alert(error))
}

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

const renderMessage = (message) => {
  return <Text>{message}</Text>;
};

const SettingsActionsView = (props) => {
  const [message, setMessage] = useState('');
  return (
    <ContentContainer>
      {renderMessage(message)}
      {renderButton('ENROLL FOR MOBILE AUTH', () => {
        setMessage("")
        enrollMobileAuthentication(setMessage, setMessage)
      },false)}
      {renderButton('CHANGE PIN', onChangePinPressed, false)}
      {renderButton('CHANGE AUTHENTICATION', props.onChangeAuthPressed, false)}
    </ContentContainer>
  );
};

SettingsActionsView.propTypes = {
  onChangeAuthPressed: PropTypes.func.isRequired,
  onChangePinPressed: PropTypes.func.isRequired,
};

const styles = StyleSheet.create({
  button: {
    marginVertical: 14,
  },
});

export default SettingsActionsView;
