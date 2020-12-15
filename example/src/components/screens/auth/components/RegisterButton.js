import React, {useState, useEffect} from 'react';
import {StyleSheet, Text, View, Linking} from 'react-native';
import PropTypes from 'prop-types';
import AsyncStorage from '@react-native-async-storage/async-storage';
import Button from '../../../general/Button';
import Switch from '../../../general/Switch';
import OneginiSdk from 'react-native-sdk-beta';
import CustomRegistrationChooserView from '../CustomRegistrationChooserView';

const startRegister = async (
    providerId,
    setRegistering,
    setError,
    onRegisterSuccess = () => null,
) => {
    setError(null);
    setRegistering(true);

    try {
        await OneginiSdk.registerUser(providerId);
        setRegistering(false);
        onRegisterSuccess();
    } catch (e) {
        setRegistering(false);
        setError(e.message ? e.message : 'Something strange happened');
    }
};

//@todo add providers selector
const RegisterButton = (props) => {
  const [isDefaultProvider, setIsDefaultProvider] = useState(true);
  const [isRegistering, setRegistering] = useState(false);
  const [linkUri, setLinkUri] = useState(null);
  const [error, setError] = useState(null);
  const [isShownCustomRegistration, setShowCustomRegistration] = useState(
    false,
  );

  useEffect(() => {
    const handleOpenURL = (event) => {
      if (event.url.substr(0, event.url.indexOf(':')) === linkUri) {
        OneginiSdk.handleRegistrationCallback(event.url);
      }
    };

    const getLinkUri = async () => {
      let uri = await AsyncStorage.getItem('@redirectUri');
      setLinkUri(uri);
    };

    if (linkUri) {
      Linking.addListener('url', handleOpenURL);
    } else {
      getLinkUri();
    }

    return () => Linking.removeListener('url', handleOpenURL);
  }, [linkUri]);

  return (
    <View style={styles.container}>
      {isShownCustomRegistration && !isRegistering ? (
        <CustomRegistrationChooserView
          onProviderSelected={(idProvider) =>
            startRegister(
              idProvider,
              setRegistering,
              setError,
              props.onRegistered,
            )
          }
        />
      ) : (
        <View />
      )}
      <Button
        name={
          isRegistering
            ? 'CANCEL'
            : isDefaultProvider
            ? 'REGISTER'
            : 'REGISTER WITH...'
        }
        onPress={() =>
          isRegistering
            ? OneginiSdk.cancelRegistration() : isDefaultProvider
            ? startRegister(null, setRegistering, setError, props.onRegistered)
            : setShowCustomRegistration(!isShownCustomRegistration)
        }
      />
      <Switch
        containerStyle={styles.switch}
        label={'USE DEFAULT IDENTITY PROVIDER'}
        onSwitch={() =>
          setIsDefaultProvider((previousState) => {
            if (!previousState) {
              setShowCustomRegistration(false);
            }
            return !previousState;
          })
        }
        value={isDefaultProvider}
      />
      <Text style={styles.errorText}>{error}</Text>
    </View>
  );
};

RegisterButton.propTypes = {
  onRegistered: PropTypes.func.isRequired,
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
  },
  switch: {
    marginTop: 10,
  },
  errorText: {
    marginTop: 10,
    fontSize: 15,
    color: '#c82d2d',
  },
});

export default RegisterButton;
