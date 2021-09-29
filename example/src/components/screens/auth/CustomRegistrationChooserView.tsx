import React, {useState, useEffect} from 'react';
import {StyleSheet, View} from 'react-native';
import Button from '../../general/Button';
import OneginiSdk, {Types} from 'onegini-react-native-sdk';
import AppColors from '../../constants/AppColors';

interface Props {
  onProviderSelected?: (providerId: string) => void;
}

const CustomRegistrationChooserView: React.FC<Props> = (props) => {
  const [providers, setProviders] = useState<Types.IdentityProvider[]>([]);

  useEffect(() => {
    // ugly!
    let isMounted = true;

    OneginiSdk.getIdentityProviders().then((identityProviders) => {
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
    <View style={styles.container}>
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
    </View>
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
  },
  buttonConteiner: {
    paddingTop: 40,
  },
});

export default CustomRegistrationChooserView;
