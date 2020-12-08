import React, {useState, useEffect} from 'react';
import {StyleSheet, Text, View} from 'react-native';
import PropTypes from 'prop-types';
import Button from '../../../general/Button';
import OneginiSdk from 'react-native-sdk-beta';

const AuthButton = (props) => {
  const [isDefaultProvider, setIsDefaultProvider] = useState(true);
  const [isDisable, setDisable] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    hasProfile()
      .then((result) => setDisable(result))
      .catch((err) => setError(err));
  }, [error]);

  return (
    <View style={styles.container}>
      <Button
        name={isDefaultProvider ? 'LOG IN' : 'LOG IN WITH...'}
        disabled={isDisable}
        onPress={() => onPressClicked(props.onAuthorized, setError)}
      />
      <Text style={styles.errorText}>{error}</Text>
    </View>
  );
};

function onPressClicked(onAuthorized, setError) {
  OneginiSdk.getUserProfiles().then((profiles) =>
    OneginiSdk.authenticateUser(profiles[0].profileId)
      .then((result) => {
        onAuthorized(true);
      })
      .catch((error) => {
        setError('' + error);
      }),
  );
}

function hasProfile() {
  return new Promise((resolve, reject) =>
    OneginiSdk.getUserProfiles()
      .then((profiles) => {
        resolve(profiles.length === 0);
      })
      .catch((err) => {
        reject(`Failed to get profiles: ${err}`);
      }),
  );
}

AuthButton.propTypes = {
  onAuthorized: PropTypes.func.isRequired,
};

const styles = StyleSheet.create({
  container: {
    marginTop: '30%',
    width: '100%',
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

export default AuthButton;
