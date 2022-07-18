import React, { useState, useEffect } from 'react';
import { StyleSheet, View, Modal, Text, TextInput } from 'react-native';
import PropTypes from 'prop-types';
import OneginiSdk, {
  ONEGINI_SDK_EVENTS,
  MOBILE_AUTH_OTP_NOTIFICATION,
} from 'react-native-sdk-beta';
import RegistrationManager from '../../managers/RegistrationManager';
import ObjectIdHelper from '../../../components/helpers/ObjectIdHelper';
import AppColors from '../../constants/AppColors'
import Button from '../../general/Button'

const MobileAuthOTPModal = (props) => {
  const [id, setId] = useState(ObjectIdHelper.getNewID("MobileAuthOTPModal"));
  const [message, setMessage] = useState("");
  const [visible, setVisible] = useState(false);


  useEffect(() => {
    OneginiSdk.addEventListener(
      ONEGINI_SDK_EVENTS.ONEGINI_MOBILE_AUTH_OTP_NOTIFICATION,
      (event) => {
        switch (event.action) {
          case MOBILE_AUTH_OTP_NOTIFICATION.START_AUTHENTICATION:
            setMessage(event.mobileAuthenticationRequest.message)
            setVisible(true)
            break;
          case MOBILE_AUTH_OTP_NOTIFICATION.FINISH_AUTHENTICATION:
            setVisible(false)
            break;
        }
      },
    );

    return () => {
      OneginiSdk.removeEventListener(
        ONEGINI_SDK_EVENTS.ONEGINI_MOBILE_AUTH_OTP_NOTIFICATION,
      );
    };
  }, []);

  return (
    <Modal
      transparent={false}
      animationType="fade"
      visible={visible}
      onRequestClose={() => setVisible(false)}>
      <View style={styles.container}>
        <Text style={styles.title}>
          {"Mobile Auth OTP"}
        </Text>
        <Text style={styles.message}>
          {message}
        </Text>
        <View style={styles.buttonContainer}>
          <Button name={"OK"} onPress={() => {
            OneginiSdk.submitAcceptMobileAuthOtp()
            setVisible(false)
          }} />
        </View>
        <View style={styles.buttonContainer}>
          <Button name={"CANCEL"} onPress={() => {
            OneginiSdk.submitDenyMobileAuthOtp()
            setVisible(false)
          }} />
        </View>
      </View>
    </Modal>
  );
};

const styles = StyleSheet.create({
  container: {
    width: '100%',
    height: '100%',
    backgroundColor: AppColors.white,
    alignItems: 'center',
    justifyContent: 'flex-start',
    paddingHorizontal: 20,
  },
  title: {
    color: AppColors.blue,
    fontSize: 32,
    marginTop: '10%',
  },
  message: {
    fontSize: 16,
    marginTop: '10%',
  },
  buttonContainer: {
    width: '60%',
    marginTop: 20,
  },
});

export default MobileAuthOTPModal;
