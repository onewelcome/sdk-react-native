import React, {useState, useEffect} from 'react';
import {Modal, StyleSheet, Text, View} from 'react-native';
import Button from '../../general/Button';
import OneWelcomeSdk, {Types} from 'onewelcome-react-native-sdk';
import AppColors from '../../constants/AppColors';

interface Props {
  onProviderSelected?: (providerId: string) => void;
  onCancelPressed?: () => void;
}

const CustomRegistrationChooserView: React.FC<Props> = (props) => {
  const [providers, setProviders] = useState<Types.IdentityProvider[]>([]);

  useEffect(() => {
    // ugly!
    let isMounted = true;

    OneWelcomeSdk.getIdentityProviders().then((identityProviders) => {
      console.log('identityProviders: ', identityProviders);
      if (isMounted) {
        setProviders(identityProviders);
      }
    });

    return () => {
      isMounted = false;
    };
  }, []);

  return (
      <Modal transparent={false}>
        <View style={styles.container}>
          <Text style={styles.title}>Register with ...</Text>
          {providers?.map((provider) => {
            return (
                <View key={provider.id} style={styles.buttonConteiner}>
                  <Button
                      name={provider.name + '(' + provider.id + ')'}
                      onPress={() => {
                        props.onProviderSelected?.(provider.id);
                      }}
                  />
                </View>
            );
          })}

          <View style={styles.buttonConteiner}>
            <Button
                name='Cancel'
                onPress={() => {
                  props.onCancelPressed?.();
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
    backgroundColor: AppColors.white,
    flex: 1,
    paddingTop: '10%',
    marginBottom: 80,
    overflow: 'hidden',
    paddingHorizontal: '4%'
  },
  title: {
    color: AppColors.blue,
    fontSize: 32,
    marginTop: '10%',
  },
  buttonConteiner: {
    flex:1,
  },
});

export default CustomRegistrationChooserView;
