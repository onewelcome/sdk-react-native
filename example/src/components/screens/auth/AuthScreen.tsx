import React, {useCallback, useEffect, useState} from 'react';
import {Image, StyleSheet, Text, View} from 'react-native';
import {Assets} from '../../../../assets';
import RegisterButton from './components/RegisterButton';
import AuthButton from './components/AuthButton';
import Button from '../../general/Button';
import InfoView from '../info/InfoView';
import OneginiSdk from "onegini-react-native-sdk";
import {CurrentUser} from "../../../auth/auth";

interface Props {
    onAuthorized?: (success?: boolean) => void;
}

async function hasProfile(): Promise<boolean> {
    const userProfiles = await OneginiSdk.getUserProfiles();
    return userProfiles.length > 0;
}

const AuthScreen: React.FC<Props> = (props) => {
    const [isInfoVisible, setIsInfoVisible] = useState(false);
    const [error, setError] = useState('');

    const authenticateUser = useCallback(async () => {
        try {
            const userProfiles = await OneginiSdk.getUserProfiles();
            if (!userProfiles.length) return;
            // TODO: Make dynamic
            const {profileId} = userProfiles[0];

            const authenticated = await OneginiSdk.authenticateUser(profileId);
            if (!authenticated) return;
            CurrentUser.id = profileId;

            props.onAuthorized?.(true);
        } catch (e) {
            setError(e.message);
        }
    }, []);

    useEffect(() => {
        try {
            hasProfile().then(() => authenticateUser());
        } catch (e) {
            setError(e.message);
        }
    }, [])

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
                <AuthButton onAuthorized={props.onAuthorized}/>
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
