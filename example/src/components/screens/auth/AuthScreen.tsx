import React, {useState} from 'react';
import {Image, StyleSheet, Text, View} from 'react-native';
import {Assets} from '../../../../assets';
import RegisterButton from './components/RegisterButton';
import AuthButton from './components/AuthButton';
import Button from '../../general/Button';
import InfoView from '../info/InfoView';

interface Props {
  onAuthorized?: (success?: boolean) => void;
}

const AuthScreen: React.FC<Props> = (props) => {
  const [isInfoVisible, setIsInfoVisible] = useState(false);

  return isInfoVisible ? (
    <InfoView onFinished={() => setIsInfoVisible(false)} />
  ) : (
    <View style={styles.container}>
      <View style={styles.logoHolder}>
        <Image source={Assets.logo} />
        <Text style={styles.logoText}>Example App</Text>
      </View>

      <AuthButton onAuthorized={props.onAuthorized} />

      <View style={styles.registerContainer}>
        <RegisterButton onRegistered={props.onAuthorized} />
      </View>
      <View style={styles.infoContainer}>
        <Button
          name="INFO"
          onPress={() => {
            setIsInfoVisible(true);
          }}
        />
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    paddingTop: '10%',
    paddingHorizontal: '15%',
  },
  logoHolder: {
    width: '100%',
    height: '15%',
    alignItems: 'center',
  },
  logo: {
    width: '100%',
    height: '100%',
    resizeMode: 'contain',
  },
  logoText: {
    position: 'absolute',
    bottom: '20%',
    right: '8%',
    color: '#777777',
    fontSize: 20,
    fontWeight: '400',
  },
  registerContainer: {
    position: 'absolute',
    bottom: '20%',
    width: '100%',
    alignSelf: 'center',
  },
  infoContainer: {
    position: 'absolute',
    bottom: '8%',
    width: '100%',
    alignSelf: 'center',
  },
  errorText: {
    marginTop: 10,
    fontSize: 15,
    color: '#c82d2d',
  },
});

export default AuthScreen;
