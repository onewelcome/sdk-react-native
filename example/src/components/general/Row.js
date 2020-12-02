import React from 'react';
import {StyleSheet, View} from 'react-native';

const Row = (props) => {
  return (
    <View style={[styles.container, props.containerStyle]}>
      {props.children}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 10,
  },
});

export default Row;
