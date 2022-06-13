import React from 'react';
import {Modal, StyleSheet, Text, View} from 'react-native';
import {Events, usePinFlow} from 'onewelcome-react-native-sdk';
import PinInput from './PinInput';
import PinKeyboard from './PinKeyboard';
import Button from '../../general/Button';

const getTitle = (flow: Events.PinFlow) => {
  switch (flow) {
    case Events.PinFlow.Create:
      return 'Create Pin';
    case Events.PinFlow.Change:
      return 'Change Pin';
    case Events.PinFlow.Authentication:
      return 'Current Pin';
    default:
      return 'Create Pin';
  }
};

const PinModal: React.FC<{}> = () => {
  const {
    flow,
    pin,
    visible,
    isConfirmMode,
    error,
    provideNewPinKey,
    cancelPinFlow,
    userInfo,
    pinLength
  } = usePinFlow();

  const title = isConfirmMode ? 'Confirm Pin' : getTitle(flow);

  return (
    <Modal
      transparent={false}
      animationType="fade"
      visible={visible}
      onRequestClose={() => {
        console.log('PIN MODAL: ON REQUEST CLOSE');
        cancelPinFlow();
      }}>
      <View style={styles.container}>
        <View style={styles.topContainer}>
          <Text style={styles.title}>{title}</Text>
          <PinInput currentPinLength={pin.length} requiredPinLength={pinLength || 5}/>
          {error && <Text style={styles.error}>{`${userInfo && userInfo?.['remainingFailureCount'] ? `Pin is incorrect, you have ${userInfo?.['remainingFailureCount']} attempts remaining` : `${error}`}`}</Text>}
        </View>
        <View style={styles.bottomContainer}>
          <PinKeyboard
            pinLength={pin.length}
            onPress={(newKey) => provideNewPinKey(newKey)}
          />
          <Button
            name={'Cancel'}
            containerStyle={styles.buttonStyle}
            onPress={() => cancelPinFlow()}
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
