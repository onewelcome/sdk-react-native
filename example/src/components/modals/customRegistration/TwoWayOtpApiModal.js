import React, {useState, useEffect} from 'react';
import {StyleSheet, View, Modal, Text, TextInput} from 'react-native';
import OneginiSdk, {
  CUSTOM_REGISTRATION_NOTIFICATIONS,
  CUSTOM_REGISTRATION_ACTIONS
} from 'react-native-sdk-beta';
import RegistrationManager from '../../managers/RegistrationManager';
import ObjectIDHelper from '../../helpers/ObjectIdHelper';
import AppColors from '../../constants/AppColors';
import Button from '../../general/Button';


const TwoWayOtpApiModal = (props) => {
  const idProvier = '2-way-otp-api';
  const [id, setId] = useState(ObjectIDHelper.getNewID('TwoWayOtpApiModal'));
  const [codeFromOnegini, setCodeFromOnegini] = useState(null);
  const [responseCode, setResponseCode] = useState(null);
  const [visible, setVisible] = useState(false);

  useEffect(() => {
    RegistrationManager.addEventListener(id, (event) => {
      if (event.identityProviderId === idProvier) {
        switch (event.action) {
          case CUSTOM_REGISTRATION_NOTIFICATIONS.INIT_REGISTRATION:
            OneginiSdk.submitCustomRegistrationAction(
              CUSTOM_REGISTRATION_ACTIONS.PROVIDE_TOKEN,
              event.identityProviderId,
              null,
            );
            break;
          case CUSTOM_REGISTRATION_NOTIFICATIONS.FINISH_REGISTRATION:
            setCodeFromOnegini(event.customInfo.data);
            setVisible(true);
            break;
        }
      }
    });

    return () => {
      RegistrationManager.removeEventListener(id);
    };
  }, [id]);

  return (
    <Modal
      transparent={false}
      animationType="fade"
      visible={visible}
      onRequestClose={() => setVisible(false)}>
      <View style={styles.container}>
        <Text style={styles.title}>{'2-way-otp-api'}</Text>
        <Text style={styles.cahalangeCode}>{'Cahalange Code: '}</Text>
        <Text style={styles.codeFromOnegini}>{codeFromOnegini}</Text>
        <Text style={styles.responseCodeTitle}>{'Response Code:'}</Text>
        <TextInput
          keyboardType={'numeric'}
          maxLength={6}
          style={styles.responseCodeInput}
          onChangeText={(text) => {
            setResponseCode(text);
          }}
        />
        <View style={styles.buttonContainer}>
          <Button
            name={'OK'}
            onPress={() => {
              OneginiSdk.submitCustomRegistrationAction(
                CUSTOM_REGISTRATION_ACTIONS.PROVIDE_TOKEN,
                idProvier,
                responseCode,
              );
              setVisible(false);
            }}
          />
        </View>
        <View style={styles.buttonContainer}>
          <Button
            name={'CANCEL'}
            onPress={() => {
              OneginiSdk.submitCustomRegistrationAction(
                CUSTOM_REGISTRATION_ACTIONS.CANCEL,
                idProvier,
                'Cancelled by user',
              );
              setVisible(false);
            }}
          />
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
  codeFromOnegini: {
    color: AppColors.black,
    fontSize: 32,
    marginTop: '1%',
  },
  cahalangeCode: {
    fontSize: 16,
    marginTop: '10%',
  },
  responseCodeInput: {
    borderColor: AppColors.black,
    width: 150,
    borderWidth: 1,
    marginTop: '10%',
  },
  responseCodeTitle: {
    fontSize: 16,
    marginTop: '10%',
  },
  buttonContainer: {
    width: '60%',
    marginTop: 20,
  },
});

export default TwoWayOtpApiModal;
