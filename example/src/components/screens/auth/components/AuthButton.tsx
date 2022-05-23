import React, {useState, useEffect} from 'react';
import {StyleSheet, Text, View} from 'react-native';
import Button from '../../../general/Button';
import OneWelcomeSdk from 'onewelcome-react-native-sdk';
import {CurrentUser} from '../../../../auth/auth';

interface Props {
  onAuthorized?: (success: boolean) => void;
}

const AuthButton: React.FC<Props> = (props) => {
  const [isDefaultProvider, setIsDefaultProvider] = useState(true);
  const [isDisable, setDisable] = useState(true);
  const [error, setError] = useState<string | null>(null);

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

function onPressClicked(
  onAuthorized?: (success: boolean) => void,
  setError?: (error: string) => void,
) {
  OneWelcomeSdk.getUserProfiles().then((profiles) =>
    OneWelcomeSdk.authenticateUser(profiles[0].profileId)
      .then((result) => {
        CurrentUser.id = profiles[0].profileId;

        console.log('AUTH: ', JSON.stringify(result));
        onAuthorized?.(true);
      })
      .catch((error) => {
        setError?.('' + error);
      }),
  );
}

function hasProfile(): Promise<boolean> {
  return new Promise((resolve, reject) =>
    OneWelcomeSdk.getUserProfiles()
      .then((profiles) => {
        console.log('Profiles: ', profiles);
        resolve(profiles.length === 0);
      })
      .catch((err) => {
        reject(`Failed to get profiles: ${err}`);
      }),
  );
}

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
