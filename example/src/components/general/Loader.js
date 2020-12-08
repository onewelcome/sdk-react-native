import React from 'react';
import {ActivityIndicator, StyleSheet, Text} from 'react-native';

const Loader = (props) => {
  return (
    <>
      <ActivityIndicator animating={true} color={'#000'} size={'large'} />
      <Text style={styles.text}>{props.message}</Text>
    </>
  );
};

const styles = StyleSheet.create({
  text: {
    fontSize: 18,
    marginTop: 10,
  },
});

export default Loader;
