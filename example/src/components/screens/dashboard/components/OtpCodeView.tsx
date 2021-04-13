import React, {useState} from 'react';
import {StyleSheet, Text, View, TextInput} from 'react-native';
import ContentContainer from './ContentContainer';
import AppColors from '../../../constants/AppColors';
import Button from '../../../general/Button';
import {handleMobileAuthWithOtp} from '../../../helpers/MobileAuthenticationHelper';

const renderMessage = (message: string) => {
  return <Text style={styles.message}>{message}</Text>;
};

const OtpCodeView: React.FC<{}> = () => {
  const [otpCode, setOtpCode] = useState('');
  const [message, setMessage] = useState('');

  return (
    <ContentContainer containerStyle={styles.container}>
      <View style={styles.row}>
        <Text style={styles.label}>OTP CODE</Text>
        <TextInput
          style={styles.otpCodeInput}
          onChangeText={(text) => {
            setOtpCode(text);
          }}
        />
        {renderMessage(message)}
        <Button
          name={'OK'}
          containerStyle={styles.okButton}
          onPress={() => {
            setMessage('');
            handleMobileAuthWithOtp(otpCode, setMessage, setMessage);
          }}
        />
      </View>
    </ContentContainer>
  );
};

const styles = StyleSheet.create({
  container: {
    paddingHorizontal: '6%',
    paddingTop: '4%',
  },
  row: {
    justifyContent: 'center',
    alignItems: 'center',
  },
  label: {
    justifyContent: 'center',
    color: '#1e8dca',
    fontSize: 22,
    fontWeight: '500',
  },
  otpCodeInput: {
    borderColor: AppColors.black,
    width: '80%',
    borderWidth: 1,
    marginTop: '10%',
    justifyContent: 'center',
    alignItems: 'center',
    margin: 10,
  },
  message: {
    margin: 15,
  },
  okButton: {
    justifyContent: 'center',
    alignItems: 'center',
  },
});

export default OtpCodeView;
