import React from 'react';
import { Modal, StyleSheet, Text, View } from 'react-native';
import { usePinFlow, ONEGINI_PIN_FLOW } from "react-native-sdk-beta/js/pin";
import PinInput from "./PinInput";
import PinKeyboard from "./PinKeyboard";
import Button from "../../general/Button";

const getTitle = (flow) => {
  switch (flow){
    case ONEGINI_PIN_FLOW.CREATE:
      return 'Create Pin';
    case ONEGINI_PIN_FLOW.CHANGE:
      return 'Change Pin';
    case ONEGINI_PIN_FLOW.AUTHENTICATION:
      return 'Current Pin';
    default:
      return 'Create Pin';
  }
}

const PinModal = (props) => {
  const [ flow, pin, visible, isConfirmMode, error, provideNewPinKey, cancelPinFlow] = usePinFlow();
  const title = isConfirmMode ? 'Confirm Pin' : getTitle(flow);

  return (
    <Modal
      transparent={false}
      animationType="fade"
      visible={visible}
      onRequestClose={() => {
        console.log('PIN MODAL: ON REQUEST CLOSE')
        cancelPinFlow()
      }}>
        <View style={styles.container}>
          <View style={styles.topContainer}>
            <Text style={styles.title}>{title}</Text>
            <PinInput pinLength={pin.length} />
            {error && <Text style={styles.error}>{error}</Text>}
          </View>
          <View style={styles.bottomContainer}>
            <PinKeyboard pinLength={pin.length} onPress={(newKey) => provideNewPinKey(newKey)}/>
            <Button name={'Cancel'} containerStyle={styles.buttonStyle} onPress={() => cancelPinFlow()}/>
          </View>
        </View>
    </Modal>
  );
};

const styles = StyleSheet.create({
  container: {
    width: '100%',
    height: '100%',
    backgroundColor: '#cacaca',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 20,
  },
  topContainer: {
    flex: 1,
    alignItems: 'center',
  },
  title: {
    fontSize: 32,
    marginTop: '10%',
  },
  error: {
    marginTop: '8%',
    fontSize: 22,
    color: '#c82d2d',
    textAlign: 'center',
  },
  bottomContainer: {
    flex: 1,
    width: '100%',
    alignItems: 'center',
    justifyContent: 'flex-end',
    paddingBottom: 20,
  },
  buttonStyle: {
    backgroundColor: '#2aa4dd',
    width: '80%',
    marginTop: 20,
  },
});

export default PinModal;
