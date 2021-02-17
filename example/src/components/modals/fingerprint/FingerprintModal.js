import React from 'react';
import { StyleSheet, View, Modal, Text } from 'react-native';
import {
  useFingerprintFlow,
  ONEGINI_FINGERPRINT_STAGE
} from 'react-native-sdk-beta/js/fingerprint';
import AppColors from '../../constants/AppColors'
import Button from '../../general/Button'

const MESSAGE_BY_STAGE = {
  [ONEGINI_FINGERPRINT_STAGE.IDLE]: 'Waiting for fingerprint flow trigger...',
  [ONEGINI_FINGERPRINT_STAGE.STARTED]: 'Use Fingerprint Sensor',
  [ONEGINI_FINGERPRINT_STAGE.NEXT_ATTEMPT]: 'Try again...',
  [ONEGINI_FINGERPRINT_STAGE.CAPTURED]: 'Verifying...',
  [ONEGINI_FINGERPRINT_STAGE.FINISHED]: 'Done',
};

const FingerprintModal = (props) => {
  const [active, stage, fallbackToPin, cancelFlow] = useFingerprintFlow();
  const message = MESSAGE_BY_STAGE[stage];

  return (
    <Modal
      transparent={false}
      animationType="fade"
      visible={active}
      onRequestClose={() => null}>
      <View style={styles.container}>
        <Text style={styles.title}>
          {"Confirm with fingerprint"}
        </Text>
        <Text style={styles.message}>
          {message}
        </Text>
        <View style={styles.buttonContainer}>
          <Button name={"USE PIN CODE"} onPress={() => fallbackToPin()} />
        </View>
        <View style={styles.buttonContainer}>
          <Button name={"CANCEL"} onPress={() => cancelFlow()} />
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
