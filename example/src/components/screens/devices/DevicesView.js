import React, {useEffect, useState} from 'react';
import {StyleSheet, Text, View, ScrollView} from 'react-native';
import AppColors from "../../constants/AppColors";
import PropTypes from "prop-types";
import OneginiSdk from 'react-native-sdk-beta';
import ContentContainer from "../dashboard/components/ContentContainer";

const DevicesView = (props) => {
    const [isLoading, setLoading] = useState(true);
    const [devices, setDevices] = useState({
        devices: [{
            "id": "",
            name: "",
            "application": "",
            "platform": "",
            "mobileAuthenticationEnabled": false
        }]
    });

    useEffect(() => {
        OneginiSdk.getClientResource()
            .then((it) => {
                setDevices(it)
                setLoading(false)
            })
            .catch((e) => {
                alert(e.message)
                setLoading(false)
            })
    }, []);


    const renderLoading = () => {
        return (
            <View style={styles.container}>
                <Text style={styles.loading}>loading ... .</Text>
            </View>
        );
    };

    //  public String getDeviceFullInfo() {
//    return getName() + Constants.NEW_LINE + getApplication() + Constants.NEW_LINE + getPlatform() + Constants.NEW_LINE
//        + "Mobile authentication enabled: " + isMobileAuthenticationEnabled();
//  }

    const base = () => {
        return (
            <ScrollView style={styles.container}>
                <View>
                    {devices.devices.map(it => {
                        return (
                            <View style={styles.row}>
                                <Text style={styles.info}>{"name: " + it.name}</Text>
                                <Text style={styles.info}>{"application: " + it.application}</Text>
                                <Text style={styles.info}>{"platform: " + it.platform}</Text>
                            </View>
                        )
                    })}
                    {/*<View style={styles.row}>*/}
                    {/*    <Text style={styles.label}>User Info</Text>*/}
                    {/*    <Text style={styles.info}>{"Profile id:  " + profileId}</Text>*/}
                    {/*    <Text style={styles.info}>{"Implicit details: " + implicitDetails}</Text>*/}
                    {/*</View>*/}
                    {/*<View style={styles.row}>*/}
                    {/*    <Text style={styles.label}>Device details</Text>*/}
                    {/*    <Text style={styles.info}>{"Application ID: " + applicationDetails.applicationIdentifier}</Text>*/}
                    {/*    <Text style={styles.info}>{"Application Version: " + applicationDetails.applicationVersion}</Text>*/}
                    {/*    <Text style={styles.info}>{"Platform: " + applicationDetails.applicationPlatform}</Text>*/}
                    {/*</View>*/}
                    {/*<View style={styles.cancelButton}>*/}
                    {/*    <Button name={"CANCEL"} onPress={props.onFinished}/>*/}
                    {/*</View>*/}
                </View>
            </ScrollView>
        );
    };

    return isLoading ? (
        renderLoading()
    ) : (
        base()
    );
};

DevicesView.propTypes = {
    onFinished: PropTypes.func.isRequired,
};

const styles = StyleSheet.create({
    container: {
        top: '10%',
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
    loading: {
        alignItems: 'center',
        justifyContent: 'center',
        color: AppColors.blue,
        fontSize: 24,
        fontWeight: '500',
    },

    loadingContainer: {
        top: '10%',
        alignItems: 'center',
        justifyContent: 'center',
    },
    info: {
        justifyContent: 'center',
        color: AppColors.black,
        fontSize: 12,
        fontWeight: '500',
        padding: 6,
    }
});

export default DevicesView;
