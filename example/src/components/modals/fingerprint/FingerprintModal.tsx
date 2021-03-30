import React, {useState, useEffect} from 'react';
import {StyleSheet, View, Modal, Text, TextInput} from 'react-native';
import OneginiSdk, {Types, Events} from 'react-native-sdk-beta';
import ObjectIdHelper from '../../helpers/ObjectIdHelper';
import AppColors from '../../constants/AppColors';
import Button from '../../general/Button';

const FingerprintModal: React.FC<{}> = () => {
  const [id, setId] = useState(ObjectIdHelper.getNewID('FingerprintModal')); // Not used. Then why is it here?
  const [message, setMessage] = useState('Touch sensor');
  const [visible, setVisible] = useState(false);

  useEffect(() => {
    const listener = OneginiSdk.addEventListener(
      Events.SdkNotification.Fingerprint,
      (event: any) => {
        switch (event.action) {
          case Events.FingerprintNotification.StartAuthentication:
            setVisible(true);
            OneginiSdk.submitFingerprintAcceptAuthenticationRequest();
            setMessage('Touch sensor');
            break;
          case Events.FingerprintNotification.OnNextAuthenticationAttempt:
            setMessage('Try again…');
            break;
          case Events.FingerprintNotification.OnFingerprintCaptured:
            setMessage('Verifying…');
            break;
          case Events.FingerprintNotification.FinishAuthentication:
            setVisible(false);
            break;
        }
      },
    );

    return () => {
      listener.remove();
    };
  }, []);

  return (
    <Modal
      transparent={false}
      animationType="fade"
      visible={visible}
      onRequestClose={() => setVisible(false)}>
      <View style={styles.container}>
        <Text style={styles.title}>{'Confirm with fingerprint'}</Text>
        <Text style={styles.message}>{message}</Text>
        <View style={styles.buttonContainer}>
          <Button
            name={'USE PIN CODE'}
            onPress={() => {
              OneginiSdk.submitFingerprintFallbackToPin();
              setVisible(false);
            }}
          />
        </View>
        <View style={styles.buttonContainer}>
          <Button
            name={'CANCEL'}
            onPress={() => {
              OneginiSdk.submitFingerprintDenyAuthenticationRequest();
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
