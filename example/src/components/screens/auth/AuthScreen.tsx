import React, {useCallback, useContext, useEffect, useRef, useState} from 'react';
import {Image, StyleSheet, Text, View} from 'react-native';
import {Assets} from '../../../../assets';
import RegisterButton from './components/RegisterButton';
import AuthButton from './components/AuthButton';
import Button from '../../general/Button';
import InfoView from '../info/InfoView';
import OneWelcomeSdk from "onewelcome-react-native-sdk";
import {CurrentUser} from "../../../auth/auth";
import ModalSelector from "react-native-modal-selector";
import {AuthContext} from "../../../providers/auth.provider";
import {AuthActionTypes} from "../../../providers/auth.actions";

interface Props {
    onAuthorized?: (success?: boolean) => void;
}

async function fetchProfiles(): Promise<boolean> {
    const userProfiles = await OneWelcomeSdk.getUserProfiles();
    return userProfiles.length > 0;
}

const AuthScreen: React.FC<Props> = (props) => {
    const {
        state: {authenticated: {loading, profiles}},
        dispatch,
    } = useContext(AuthContext);
    const [isInfoVisible, setIsInfoVisible] = useState(false);
    const [error, setError] = useState('');
    const [selectedProfileId, setSelectedProfileId] = useState<string | null>(null);
    // because react-native-modal-selector is broken and calls onChange when unmount
    const isModalOpen = useRef(false);

    const authenticateProfile = useCallback(async (id: string) => {
        try {
            const authenticated = await OneWelcomeSdk.authenticateUser(id);
            if (!authenticated) return;
            CurrentUser.id = id;

            props.onAuthorized?.(true);
        } catch (e: any) {
            setError(e.message);
        }
    }, []);

    const fetchProfiles = useCallback(async () => {
        try {
            dispatch({type: AuthActionTypes.AUTH_LOAD_PROFILE_IDS});
            const userProfiles = await OneWelcomeSdk.getUserProfiles();
            dispatch({
                type: AuthActionTypes.AUTH_SET_PROFILE_IDS,
                payload: userProfiles?.map(({profileId}) => profileId) || []
            });
        } catch (e: any) {
            setError(e.message);
            dispatch({type: AuthActionTypes.AUTH_SET_PROFILE_IDS, payload: []});
        }
    }, [dispatch, setError]);

    useEffect(() => {
        if (!loading) {
            fetchProfiles();
        }
    }, []);

    useEffect(() => {
        if (!profiles && !loading) {
            fetchProfiles();
        }
    }, [profiles, loading, fetchProfiles]);

    return isInfoVisible ? (
        <InfoView onFinished={() => setIsInfoVisible(false)}/>
    ) : (
        basView()
    );

    function basView() {
        return (
            <View style={styles.container}>
                <View style={styles.logoHolder}>
                    <Image source={Assets.logo}/>
                    <Text style={styles.logoText}>Example App</Text>
                </View>
                <Text style={styles.errorText}>{error}</Text>

                {profiles && profiles.length > 1 ? (
                    <ModalSelector
                        data={profiles.map(p => p.id)}
                        initValue={profiles[0].id}
                        selectedKey={profiles[0].id}
                        keyExtractor={(item) => item}
                        labelExtractor={(item) => item}
                        selectedItemTextStyle={{fontWeight: '700'}}
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

                        <Button name='LOG IN WITH ...'>Log in</Button>
                    </ModalSelector>) : (<AuthButton onAuthorized={props.onAuthorized}/>)
                }

                <View style={styles.registerContainer}>
                    <RegisterButton onRegistered={props.onAuthorized}/>
                </View>
                <View style={styles.infoContainer}>
                    <Button
                        name={'INFO'}
                        onPress={() => {
                            setIsInfoVisible(true);
                        }}
                    />
                </View>
            </View>
        );
    }
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
