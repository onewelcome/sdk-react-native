import React, {useEffect, useState} from 'react';
import {StyleSheet, Text, View} from 'react-native';
import Button from "../../general/Button";
import ContentContainer from "../dashboard/components/ContentContainer";
import AppColors from "../../constants/AppColors";
import PropTypes from "prop-types";
import OneginiSdk from 'react-native-sdk-beta';

const InfoView = (props) => {
    const [profileId, setProfileId] = useState("");
    const [implicitDetails, setImplicitDetails] = useState("");
    const [applicationDetails, setApplicationDetails] = useState({
        "applicationIdentifier": "",
        "applicationVersion": "",
        "applicationPlatform": ""
    });

    useEffect(() => {
        OneginiSdk.getUserProfiles().then((it) => {
            var profile = it[0]
            if (profile != null) {
                setProfileId(profile.profileId)
                OneginiSdk.getImplicitUserDetails(profile.profileId).then((details) => {
                    setImplicitDetails(details)
                }).catch((e) => {
                    setImplicitDetails(e.message)
                })
            }
        })
        OneginiSdk.authenticateDevice()
            .then((it) => {
                setApplicationDetails(it)
            }).catch((e) => {
            alert(e)
        })
    }, []);

    return (
        <ContentContainer containerStyle={styles.container}>
            <View style={styles.row}>
                <Text style={styles.label}>User Info</Text>
                <Text style={styles.info}>{"Profile id:  " + profileId}</Text>
                <Text style={styles.info}>{"Implicit details: " + implicitDetails}</Text>
            </View>
            <View style={styles.row}>
                <Text style={styles.label}>Device details</Text>
                <Text style={styles.info}>{"Application ID: " + applicationDetails.applicationIdentifier}</Text>
                <Text style={styles.info}>{"Application Version: " + applicationDetails.applicationVersion}</Text>
                <Text style={styles.info}>{"Platform: " + applicationDetails.applicationPlatform}</Text>
            </View>
            <View style={styles.cancelButton}>
                <Button name={"CANCEL"} onPress={props.onFinished}/>
            </View>
        </ContentContainer>
    );
};

InfoView.propTypes = {
    onFinished: PropTypes.func.isRequired,
};

const styles = StyleSheet.create({
    container: {
        paddingHorizontal: '6%',
        paddingTop: '4%',
    },
    row: {
        paddingHorizontal: '10%',
        justifyContent: 'flex-start',
        alignItems: 'flex-start'
    },
    label: {
        justifyContent: 'center',
        color: AppColors.blue,
        fontSize: 24,
        fontWeight: '500',
    },
    info: {
        justifyContent: 'center',
        color: AppColors.black,
        fontSize: 12,
        fontWeight: '500',
        padding: 6,
    },
    cancelButton: {
        justifyContent: 'center',
        alignItems: 'center',
        marginTop: 40,
    },
});

export default InfoView;