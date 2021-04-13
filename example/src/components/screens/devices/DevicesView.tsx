import React from 'react';
import {StyleSheet, Text, View, ScrollView} from 'react-native';
import AppColors from '../../constants/AppColors';
import Layout from '../../constants/Layout';
import { DEFAULT_RESOURCE_DETAILS, useResource} from 'react-native-sdk-beta';

const renderDevice = (device: any) => {
  return (
    <View key={device['id']} style={styles.row}>
      <Text style={styles.info}>{"name: " + device['name']}</Text>
      <Text style={styles.info}>{"application: " + device['application']}</Text>
      <Text style={styles.info}>{"platform: " + device['platform']}</Text>
    </View>
  )
}

//@todo resolve this with more types for resources
const getData = (data: any, key: string) => {
  if(data[key]) {
    return data[key];
  } else {
    return `No data for key: ${key}`;
  }
}

const DevicesView: React.FC<{}> = () => {
  const {loading, data, error} = useResource(
    {...DEFAULT_RESOURCE_DETAILS, path: 'devices'},
    true
  );

  return (
    <ScrollView style={styles.container}>
      {loading && <Text style={styles.loading}>{'loading ...'}</Text>}
      {error && <Text style={[styles.loading, { color: AppColors.red }]}>{getData(error, 'message')}</Text>}
      {data && !loading && !error && (
        <View style={styles.scrollViewContainer}>
          {getData(data, 'devices')?.map((device: any) => renderDevice(device))}
        </View>
      )}
    </ScrollView>
  )
};

const styles = StyleSheet.create({
  container: {
    marginTop: Layout.window.width * 0.2,
    paddingBottom: '15%',
    paddingHorizontal: '4%',
  },
  scrollViewContainer: {
    paddingBottom: '15%',
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
  },
});

export default DevicesView;
