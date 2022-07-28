import React, {
  useState,
  useEffect,
  useCallback,
  useContext,
  useRef,
} from 'react';
import {StyleSheet, Text, View} from 'react-native';
import Button from '../../../general/Button';
import OneWelcomeSdk from 'onewelcome-react-native-sdk';
import {CurrentUser} from '../../../../auth/auth';
import {AuthContext} from '../../../../providers/auth.provider';
import {AuthActionTypes} from '../../../../providers/auth.actions';
import ModalSelector from 'react-native-modal-selector';

interface Props {
  onAuthorized?: (success: boolean) => void;
}

const AuthButton: React.FC<Props> = (props) => {
  const [error, setError] = useState<string | null>(null);
  const isModalOpen = useRef(false);
  const {
    state: {
      authenticated: {loading, profiles},
    },
    dispatch,
  } = useContext(AuthContext);

  const fetchProfiles = useCallback(async () => {
    try {
      dispatch({type: AuthActionTypes.AUTH_LOAD_PROFILE_IDS});
      const userProfiles = await OneWelcomeSdk.getUserProfiles();
      dispatch({
        type: AuthActionTypes.AUTH_SET_PROFILE_IDS,
        payload: userProfiles?.map(({profileId}) => profileId) || [],
      });
    } catch (e: any) {
      setError(e.message);
      dispatch({type: AuthActionTypes.AUTH_SET_PROFILE_IDS, payload: []});
    }
  }, [dispatch]);

  const authenticateProfile = useCallback(
    async (id: string) => {
      try {
        const authenticated = await OneWelcomeSdk.authenticateUser(id);
        if (!authenticated) {
          return;
        }
        CurrentUser.id = id;
        props.onAuthorized?.(true);
      } catch (e: any) {
        setError(e.message);
        fetchProfiles();
      }
    },
    [fetchProfiles, props],
  );

  useEffect(() => {
    if (!profiles && !loading) {
      fetchProfiles();
    }
  }, [profiles, loading, fetchProfiles]);

  return (
    <View style={styles.container}>
      {profiles && profiles.length > 1 ? (
        <ModalSelector
          data={profiles.map((p) => p.id)}
          initValue={profiles[0].id}
          selectedKey={profiles[0].id}
          keyExtractor={(item) => item}
          labelExtractor={(item) => item}
          selectedItemTextStyle={{fontWeight: '700'}}
          style={styles.modal}
          onModalClose={() => {
            isModalOpen.current = false;
          }}
          onModalOpen={() => {
            isModalOpen.current = true;
          }}
          onChange={(option) => {
            if (isModalOpen.current) {
              authenticateProfile(option);
            }
          }}>
          <Button name="LOG IN WITH ...">Log in</Button>
        </ModalSelector>
      ) : (
        <Button
          name={'LOG IN'}
          disabled={!profiles || profiles.length < 1}
          onPress={() =>
            profiles && profiles.length && authenticateProfile(profiles[0].id)
          }
        />
      )}
      <Text style={styles.errorText}>{error}</Text>
    </View>
  );
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
  modal: {
    width: '100%',
  },
});

export default AuthButton;
