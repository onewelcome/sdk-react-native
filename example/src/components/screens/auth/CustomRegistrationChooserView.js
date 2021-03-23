import React, {useState, useEffect} from 'react';
import {StyleSheet, View} from 'react-native';
import PropTypes from 'prop-types';
import Button from '../../general/Button';
import OneginiSdkTs from "react-native-sdk-beta/ts/index_ts";
import AppColors from '../../constants/AppColors';

const CustomRegistrationChooserView = (props) => {
  const [providers, setProviders] = useState(null);

  useEffect(() => {
    OneginiSdkTs.getIdentityProviders().then((identityProviders) => {
      setProviders(identityProviders);
    });
    
    
  }, []);

  return (
    <View style={styles.container}>
      {providers?.map((provider) => {
        return (
          <View key={provider.id} style={styles.buttonConteiner}>
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
    width: '100%',
    backgroundColor: AppColors.white,
    flex: 1,
    paddingTop: '10%',
    marginBottom: 80,
    overflow: 'hidden'
  },
  buttonConteiner: {
    paddingTop: 40,
  },
});

export default CustomRegistrationChooserView;
