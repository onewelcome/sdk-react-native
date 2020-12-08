import React, {useState, useEffect} from 'react';
import {StyleSheet, View} from 'react-native';
import PropTypes from 'prop-types';
import Button from '../../general/Button';
import OneginiSdk from 'react-native-sdk-beta';
import AppColors from '../../constants/AppColors';

const CustomRegistrationChooserView = (props) => {
  const [providers, setProviders] = useState(null);

  useEffect(() => {
    OneginiSdk.getIdentityProviders().then((identityProviders) => {
      setProviders(identityProviders);
    });
  }, []);

  return (
    <View style={styles.container}>
      {providers?.map((provider) => {
        return (
          <View style={styles.buttonConteiner}>
            <Button
              name={provider.name + '(' + provider.id + ')'}
              onPress={() => {
                props.onProviderSelected(provider.id);
              }}
            />
          </View>
        );
      })}
    </View>
  );
};

CustomRegistrationChooserView.propTypes = {
  onProviderSelected: PropTypes.func.isRequired,
};

const styles = StyleSheet.create({
  container: {
    backgroundColor: AppColors.white,
    flex: 1,
    paddingTop: '10%',
    marginBottom: 40,
  },
  buttonConteiner: {
    paddingTop: 40,
  },
});

export default CustomRegistrationChooserView;
