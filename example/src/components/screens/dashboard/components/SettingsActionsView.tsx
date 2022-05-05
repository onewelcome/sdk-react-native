import React, {Dispatch, useCallback, useContext, useState} from 'react';
import {Text, StyleSheet, Alert} from 'react-native';
import ContentContainer from './ContentContainer';
import Button from '../../../general/Button';
import {enrollMobileAuthentication} from '../../../helpers/MobileAuthenticationHelper';
import OneginiSdk from 'onewelcome-react-native-sdk';
import {AuthContext} from "../../../../providers/auth.provider";
import {AuthActionTypes} from "../../../../providers/auth.actions";

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
  const {dispatch} = useContext(AuthContext);
  const [message, setMessage] = useState('');

    const onChangePinPressed = useCallback(async () => {
        try {
            await OneginiSdk.changePin();
            Alert.alert('Success', 'PIN changed successfully');
        } catch (e: any) {
            if (e.code !== '9006') {
                Alert.alert('error', JSON.stringify(e));
                dispatch({type: AuthActionTypes.AUTH_SET_AUTHORIZATION, payload: false});
            }
        }
    }, [dispatch]);

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
