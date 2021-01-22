import React, { useState, useEffect } from 'react';
import { StyleSheet, View, Modal, Text, TextInput } from 'react-native';
import PropTypes from 'prop-types';
import OneginiSdk, {
  ONEGINI_SDK_EVENTS,
  MOBILE_AUTH_OTP_NOTIFICATION,
  FINGERPRINT_NOTIFICATION
} from 'react-native-sdk-beta';
import RegistrationManager from '../../managers/RegistrationManager';
import ObjectIdHelper from '../../../components/helpers/ObjectIdHelper';
import AppColors from '../../constants/AppColors'
import Button from '../../general/Button'

const FingerprintModal = (props) => {
  const [id, setId] = useState(ObjectIdHelper.getNewID("FingerprintModal"));
  const [message, setMessage] = useState("Touch sensor");
  const [visible, setVisible] = useState(false);


  useEffect(() => {
    OneginiSdk.addEventListener(
      ONEGINI_SDK_EVENTS.ONEGINI_FINGERPRINT_NOTIFICATION,
      (event) => {
        switch (event.action) {
          case FINGERPRINT_NOTIFICATION.START_AUTHENTICATION:
            setVisible(true)
            OneginiSdk.submitFingerprintAcceptAuthenticationRequest()
            setMessage("Touch sensor")
            break;
          case FINGERPRINT_NOTIFICATION.ON_NEXT_AUTHENTICATION_ATTEMPT:
            setMessage("Try again…")
            break;
          case FINGERPRINT_NOTIFICATION.ON_FINGERPRINT_CAPTURED:
            setMessage("Verifying…")
            break;
          case FINGERPRINT_NOTIFICATION.FINISH_AUTHENTICATION:
            setVisible(false)
            break;
        }
      },
    );

    return () => {
      OneginiSdk.removeEventListener(
        ONEGINI_SDK_EVENTS.ONEGINI_FINGERPRINT_NOTIFICATION,
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
          {"Confirm with fingerprint"}
        </Text>
        <Text style={styles.message}>
          {message}
        </Text>
        <View style={styles.buttonContainer}>
          <Button name={"USE PIN CODE"} onPress={() => {
            OneginiSdk.submitFingerprintFallbackToPin()
            setVisible(false)
          }} />
        </View>
        <View style={styles.buttonContainer}>
          <Button name={"CANCEL"} onPress={() => {
            OneginiSdk.submitFingerprintDenyAuthenticationRequest()
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

export default FingerprintModal;
