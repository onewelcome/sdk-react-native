import React, { useState, useEffect } from 'react';
import { StyleSheet, Text, View } from 'react-native';
import ContentContainer from './ContentContainer';
import Row from '../../../general/Row';
import Switch from '../../../general/Switch';
import { registerFingerprintAuthenticator, deregisterFingerprintAuthenticator, isFingerprintAuthenticatorRegistered } from '../../../helpers/FingerprintHelper'

const ChangeAuthView = (props) => {
  const [isFigerprintEnable, setFingerprintEnable] = useState(false);
  const [message, setMessage] = useState('');

  useEffect(() => {
    isFingerprintAuthenticatorRegistered(setFingerprintEnable)
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
        <Text style={styles.methodText}>PIN</Text>
        {/* @todo PIN/Fingerprint Clickable with Modal selector in the future */}
      </Row>
      {/* @todo Later add here errors output */}
      <View style={styles.authenticatorsHolder}>
        <Text style={styles.authenticatorsLabel}>Possible authenticators:</Text>
        <Switch
          containerStyle={styles.pinSwitchContainer}
          labelStyle={styles.switchLabel}
          label={'PIN'}
          onSwitch={() => null}
          value={true}
          disabled={true}
        />
        <Switch
          containerStyle={styles.fingerprintSwitchContainer}
          labelStyle={styles.switchLabel}
          label={'Fingerprint'}
          onSwitch={(isEnable) => onSwithFingerprint(isEnable, setFingerprintEnable, setMessage)}
          value={isFigerprintEnable}
        />
      </View>
    </ContentContainer>
  );
};

const onSwithFingerprint = (isEnable, setFigerprintEnable, setMessage) => {
  setMessage("")
  if (isEnable) {
    registerFingerprintAuthenticator((successful) => {
      if (successful) {
        setFigerprintEnable(true)
      }
    }, setMessage)
  } else {
    deregisterFingerprintAuthenticator((successful) => {
      if (successful) {
        setFigerprintEnable(false)
      }
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
