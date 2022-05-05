import React, {useState, useEffect, useRef} from 'react';
import {StyleSheet, Text, View} from 'react-native';
import ContentContainer from './ContentContainer';
import Row from '../../../general/Row';
import Switch from '../../../general/Switch';
import ModalSelector from 'react-native-modal-selector';
import {
  registerFingerprintAuthenticator,
  deregisterFingerprintAuthenticator,
  isFingerprintAuthenticatorRegistered,
  getRegisteredAuthenticators,
  setPreferredAuthenticator,
} from '../../../helpers/FingerprintHelper';
import {Types} from 'onewelcome-react-native-sdk';

const emptyRegisteredAuthenticators: Types.Authenticator[] = [
  {id: '0', name: '', isPreferred: true, isRegistered: false, type: ''},
];

const pinRegisteredAuthenticator: Types.Authenticator = {
  id: '0',
  name: 'PIN',
  isPreferred: true,
  isRegistered: false,
  type: '',
};

interface Props {}

const ChangeAuthView: React.FC<Props> = () => {
  const [isFigerprintEnable, setFingerprintEnable] = useState(false);
  const [message, setMessage] = useState('');
  const [registeredAuthenticators, setRegisteredAuthenticators] = useState<
    Types.Authenticator[]
  >(emptyRegisteredAuthenticators);
  const [allAuthenticators, setAllAuthenticators] = useState<
    Types.Authenticator[]
  >(emptyRegisteredAuthenticators);
  const [preferred, setPreferred] = useState<Types.Authenticator>(
    pinRegisteredAuthenticator,
  );

  // because react-native-modal-selector is broken and calls onChange when unmount
  const isModalOpen = useRef(false);

  useEffect(() => {
    isFingerprintAuthenticatorRegistered(setFingerprintEnable);
    getRegisteredAuthenticators(
      setRegisteredAuthenticators,
      setAllAuthenticators,
      setPreferred,
    );
  }, []);

  const hasFingerprintAuthenticator =
    allAuthenticators.findIndex(
      (auth) =>
        auth.id.toUpperCase() === 'FINGERPRINT' ||
        auth.name.toUpperCase() === 'FINGERPRINT',
    ) > -1;

  const renderMessage = (msg: string) => {
    if (msg !== '') {
      return <Text style={styles.message}>{msg}</Text>;
    } else {
      return;
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
          keyExtractor={(item) => item.id}
          labelExtractor={(item) => item.name}
          selectedItemTextStyle={{fontWeight: '700'}}
          onModalClose={() => {
            isModalOpen.current = false;
          }}
          onModalOpen={() => {
            isModalOpen.current = true;
          }}
          onChange={(option) => {
            if (isModalOpen.current) {
              onPreferredChanged(
                option,
                setMessage,
                setPreferred,
                setRegisteredAuthenticators,
              );
            }
          }}
        />
      </Row>
      <View style={styles.authenticatorsHolder}>
        <Text style={styles.authenticatorsLabel}>Possible authenticators:</Text>
        <Switch
          containerStyle={styles.pinSwitchContainer}
          labelStyle={styles.switchLabel}
          label={'PIN'}
          value={true}
          disabled={true}
        />
        {hasFingerprintAuthenticator && (
          <Switch
            containerStyle={styles.fingerprintSwitchContainer}
            labelStyle={styles.switchLabel}
            label={'Fingerprint'}
            onSwitch={(enabled: boolean) =>
              onSwithFingerprint(
                enabled,
                setFingerprintEnable,
                setMessage,
                setRegisteredAuthenticators,
                setPreferred,
              )
            }
            value={isFigerprintEnable}
          />
        )}
      </View>
    </ContentContainer>
  );
};

const onPreferredChanged = (
  preferred: Types.Authenticator,
  setMessage: (msg: string) => void,
  setPreferred: (authenticator: Types.Authenticator) => void,
  setRegisteredAuthenticators: (authenticators: Types.Authenticator[]) => void,
) => {
  setPreferredAuthenticator(
    preferred,
    (success: boolean) => {
      getRegisteredAuthenticators(
        setRegisteredAuthenticators,
        () => {},
        setPreferred,
      );
    },
    setMessage,
  );
};

const onSwithFingerprint = (
  isEnable: boolean,
  setFigerprintEnable: (enabled: boolean) => void,
  setMessage: (message: string) => void,
  setRegisteredAuthenticators: (authenticators: Types.Authenticator[]) => void,
  setPreferred: (authenticator: Types.Authenticator) => void,
) => {
  setMessage('');
  if (isEnable) {
    registerFingerprintAuthenticator((successful) => {
      if (successful) {
        setFigerprintEnable(true);
      }
      getRegisteredAuthenticators(
        setRegisteredAuthenticators,
        () => {},
        setPreferred,
      );
    }, setMessage);
  } else {
    deregisterFingerprintAuthenticator((successful) => {
      if (successful) {
        setFigerprintEnable(false);
      }
      getRegisteredAuthenticators(
        setRegisteredAuthenticators,
        () => {},
        setPreferred,
      );
    }, setMessage);
  }
};

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
