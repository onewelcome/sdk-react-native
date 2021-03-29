import React from 'react';
import {StyleSheet, View} from 'react-native';

const Bullet: React.FC<{filled: boolean}> = (props) => {
  const style = {
    ...styles.bullet,
    ...(props.filled ? {borderWidth: 0, backgroundColor: '#000'} : {}),
  };

  return <View style={style} />;
};

const PinInput: React.FC<{pinLength: number}> = (props) => {
  return (
    <View style={styles.container}>
      <Bullet filled={props.pinLength > 0} />
      <Bullet filled={props.pinLength > 1} />
      <Bullet filled={props.pinLength > 2} />
      <Bullet filled={props.pinLength > 3} />
      <Bullet filled={props.pinLength > 4} />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    width: '50%',
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: '32%',
  },
  bullet: {
    width: 28,
    height: 28,
    borderRadius: 28 / 2,
    borderWidth: 4,
    borderColor: '#000',
    marginHorizontal: 5,
  },
});

export default PinInput;
