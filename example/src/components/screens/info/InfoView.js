import React, {useEffect, useState} from 'react';
import {StyleSheet, Text, View} from 'react-native';
import Button from "../../general/Button";
import ContentContainer from "../dashboard/components/ContentContainer";
import AppColors from "../../constants/AppColors";
import PropTypes from "prop-types";
import OneginiSdk from 'react-native-sdk-beta';
import { useResource, DEFAULT_RESOURCE_DETAILS, useImplicitResource } from "../../../../../js/resource";

const handleImplicitData = async (setProfileError, profileId, setProfileId) => {
    try {
        const profiles = await OneginiSdk.getUserProfiles();

        if (profiles[0]) {
            if(profileId !== profiles[0]?.profileId) {
                setProfileId(profiles[0].profileId);
            }
        } else {
            setProfileError('No profiles registered.')
        }
    } catch (e) {
        setProfileError(e);
    }
};

const InfoView = (props) => {
    const [profileError, setProfileError] = useState(null);
    const [implicitLoading, implicitData, implicitError, profileId, setProfileId] = useImplicitResource(
      {...DEFAULT_RESOURCE_DETAILS, path: 'user-id-decorated'}
    );
    const [loading, data, error] = useResource(
      {...DEFAULT_RESOURCE_DETAILS, path: 'application-details'},
      true
    );

    useEffect(() => {
        handleImplicitData(setProfileError, profileId, setProfileId);
    }, []);

    return (
        <ContentContainer containerStyle={styles.container}>
            <View style={styles.row}>
                <Text style={styles.label}>User Info</Text>
                {implicitLoading && <Text style={styles.info}>{'Loading...'}</Text>}
                {profileError && <Text style={styles.infoError}>{'Error:  ' + profileError}</Text>}
                {implicitError && <Text style={styles.infoError}>{'Implicit Error:  ' + implicitError}</Text>}
                {profileId && <Text style={styles.info}>{"Profile id:  " + profileId}</Text>}
                {implicitData && <Text style={styles.info}>{"Implicit details: " + implicitData["decorated_user_id"]}</Text>}
            </View>
            <View style={styles.row}>
                <Text style={styles.label}>Device details</Text>
                {loading && <Text style={styles.info}>{'Loading...'}</Text>}
                {error && <Text style={styles.infoError}>{'Resource Error:  ' + error}</Text>}
                {data && (
                  <>
                      <Text style={styles.info}>{"Application ID: " + data["application_identifier"]}</Text>
                      <Text style={styles.info}>{"Application Version: " + data["application_platform"]}</Text>
                      <Text style={styles.info}>{"Platform: " + data["application_version"]}</Text>
                  </>
                )}
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
    infoError: {
        justifyContent: 'center',
        color: AppColors.red,
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
