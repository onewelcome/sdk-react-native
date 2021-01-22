import React, { useState, useEffect } from 'react';
import { StyleSheet, Text, View } from 'react-native';
import ContentContainer from './ContentContainer';
import Row from '../../../general/Row';
import Switch from '../../../general/Switch';
import ModalSelector from 'react-native-modal-selector'
import {
  registerFingerprintAuthenticator,
  deregisterFingerprintAuthenticator,
  isFingerprintAuthenticatorRegistered,
  getRegisteredAuthenticators,
  setPreferredAuthenticator
} from '../../../helpers/FingerprintHelper'

const emptyRegisteredAuthenticators = [
  { id: 0, section: true, name: '' },
];

const pinRegisteredAuthenticators = [
  { id: 0, section: true, name: 'PIN' },
];

const ChangeAuthView = (props) => {
  const [isFigerprintEnable, setFingerprintEnable] = useState(false);
  const [message, setMessage] = useState('');
  const [registeredAuthenticators, setRegisteredAuthenticators] = useState(emptyRegisteredAuthenticators);
  const [preferred, setPreferred] = useState(pinRegisteredAuthenticators);

  useEffect(() => {
    isFingerprintAuthenticatorRegistered(setFingerprintEnable)
    getRegisteredAuthenticators(setRegisteredAuthenticators, setPreferred)
  }, []);

  const renderMessage = (message) => {
    if (message != "") {
      return (
        <Text style={styles.message}>{message}</Text>
      );
    } else {
      return
    }
  };

  return (
    <ContentContainer containerStyle={styles.container}>
      {renderMessage(message)}
      <Row containerStyle={styles.row}>
        <Text style={styles.methodLabel}>Login Method</Text>
        <ModalSelector
          data={registeredAuthenticators}
          initValue={preferred.name}
          selectedKey={preferred.id}
          keyExtractor={item => item.id}
          labelExtractor={item => item.name}
          selectedItemTextStyle={{fontWeight: '700'}}
          onChange={(option) => { onPreferredChanged(option, setMessage, setPreferred, setRegisteredAuthenticators) }}
        >
        </ModalSelector>
      </Row>
      <View style={styles.authenticatorsHolder}>
        <Text style={styles.authenticatorsLabel}>Possible authenticators:</Text>
        <Switch
          containerStyle={styles.pinSwitchContainer}
          labelStyle={styles.switchLabel}
          label={'PIN'}
          onSwitch={(option) => null}
          value={true}
          disabled={true}
        />
        <Switch
          containerStyle={styles.fingerprintSwitchContainer}
          labelStyle={styles.switchLabel}
          label={'Fingerprint'}
          onSwitch={(isEnable) => onSwithFingerprint(isEnable,
            setFingerprintEnable,
            setMessage,
            setRegisteredAuthenticators,
            setPreferred)}
          value={isFigerprintEnable}
        />
      </View>
    </ContentContainer>
  );
};

const onPreferredChanged = (preferred, setMessage, setPreferred, setRegisteredAuthenticators) => {
  setPreferredAuthenticator(preferred, (successful) => {
    getRegisteredAuthenticators(setRegisteredAuthenticators, setPreferred)
  }, setMessage)
}

const onSwithFingerprint = (isEnable, setFigerprintEnable, setMessage, setRegisteredAuthenticators, setPreferred) => {
  setMessage("")
  if (isEnable) {
    registerFingerprintAuthenticator((successful) => {
      if (successful) {
        setFigerprintEnable(true)
      }
      getRegisteredAuthenticators(setRegisteredAuthenticators, setPreferred)
    }, setMessage)
  } else {
    deregisterFingerprintAuthenticator((successful) => {
      if (successful) {
        setFigerprintEnable(false)
      }
      getRegisteredAuthenticators(setRegisteredAuthenticators, setPreferred)
    }, setMessage)
  }
}

const styles = StyleSheet.create({
  container: {
    paddingHorizontal: '6%',
    paddingTop: '4%',
  },
  row: {
    justifyContent: 'space-between',
    marginTop: 0,
  },
  methodLabel: {
    color: '#1e8dca',
    fontSize: 22,
    fontWeight: '500',
  },
  methodText: {
    color: '#777777',
    fontSize: 22,
    fontWeight: '400',
  },
  authenticatorsHolder: {
    marginTop: '25%',
  },
  authenticatorsLabel: {
    color: '#1e8dca',
    fontSize: 20,
    fontWeight: '500',
    marginBottom: 20,
  },
  pinSwitchContainer: {
    paddingBottom: 10,
    borderBottomColor: '#d7d7d7',
    borderBottomWidth: StyleSheet.hairlineWidth,
  },
  fingerprintSwitchContainer: {
    paddingTop: 10,
  },
  switchLabel: {
    fontSize: 20,
    fontWeight: '500',
    color: '#7c7c7c',
  },
  message: {
    margin: 15,
  },
});

export default ChangeAuthView;
