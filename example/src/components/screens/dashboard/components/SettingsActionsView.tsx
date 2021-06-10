import React, {useState} from 'react';
import {Text, StyleSheet, Alert} from 'react-native';
import ContentContainer from './ContentContainer';
import Button from '../../../general/Button';
import {enrollMobileAuthentication} from '../../../helpers/MobileAuthenticationHelper';
import OneginiSdk from 'react-native-sdk-beta';

const onChangePinPressed = () => {
  //@todo handle deregistration error when codes will be presented
  OneginiSdk.changePin()
    .then(() => Alert.alert('Success'))
    .catch((error) => Alert.alert('error', JSON.stringify(error)));
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

const renderMessage = (message: string) => {
  return <Text>{message}</Text>;
};

interface Props {
  onChangeAuthPressed?: () => void;
  onChangePinPressed?: () => void;
}

const SettingsActionsView: React.FC<Props> = (props) => {
  const [message, setMessage] = useState('');
  return (
    <ContentContainer>
      {renderMessage(message)}
      {renderButton(
        'ENROLL FOR MOBILE AUTH',
        () => {
          setMessage('');
          enrollMobileAuthentication(setMessage, setMessage);
        },
        false,
      )}
      {renderButton('CHANGE PIN', onChangePinPressed, false)}
      {renderButton('CHANGE AUTHENTICATION', props.onChangeAuthPressed, false)}
    </ContentContainer>
  );
};

const styles = StyleSheet.create({
  button: {
    marginVertical: 14,
  },
});

export default SettingsActionsView;
