import React, {useEffect, useState} from 'react';
import {StyleSheet, Text, View, ScrollView, Dimensions} from 'react-native';
import AppColors from "../../constants/AppColors";
import PropTypes from "prop-types";
import OneginiSdk from 'react-native-sdk-beta';
import Layout from "../../constants/Layout"

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
        OneginiSdk.getDeviceListResource()
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

    const base = () => {
        return (
            <ScrollView style={styles.container}>
                <View style={styles.scrollViewContainer}>
                    {devices.devices.map(it => {
                        return (
                            <View key={it.id} style={styles.row}>
                                <Text style={styles.info}>{"name: " + it.name}</Text>
                                <Text style={styles.info}>{"application: " + it.application}</Text>
                                <Text style={styles.info}>{"platform: " + it.platform}</Text>
                            </View>
                        )
                    })}
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
        marginTop: Layout.window.width * 0.2,
        paddingBottom:'15%',
        paddingHorizontal: '4%',
    },
    scrollViewContainer: {
        paddingBottom:'15%',
    },
    row: {
        marginTop: '5%',
        borderRadius: 4,
        backgroundColor: AppColors.white,
        shadowColor: AppColors.gray,
        shadowOffset: {
            width: 0,
            height: 1,
        },
        shadowOpacity: 0.2,
        shadowRadius: 2,
        elevation: 3,
        padding: '5%',
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
