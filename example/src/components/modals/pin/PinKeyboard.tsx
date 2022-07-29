import React from 'react';
import {StyleSheet, View, TouchableOpacity, Text} from 'react-native';

const buttons = [
  '1',
  '2',
  '3',
  '4',
  '5',
  '6',
  '7',
  '8',
  '9',
  'blank',
  '0',
  '<',
];

interface KeyButtonProps {
  hidden: boolean;
  item: string;
  onPress?: (item: string) => void;
}

const KeyButton: React.FC<KeyButtonProps> = (props) => {
  const style = {
    ...styles.keyContainer,
    ...(props.hidden ? {backgroundColor: 'transparent'} : {}),
  };

  return (
    <TouchableOpacity
      activeOpacity={props.hidden ? 1 : 0.7}
      style={style}
      onPress={() => (!props.hidden ? props.onPress?.(props.item) : null)}>
      {!props.hidden && <Text style={styles.keyText}>{props.item}</Text>}
    </TouchableOpacity>
  );
};

//

interface PinKeyboardProps {
  pinLength: number;
  onPress?: (item: string) => void;
}

const PinKeyboard: React.FC<PinKeyboardProps> = (props) => {
  return (
    <View style={styles.container}>
      {buttons.map((item) => (
        <KeyButton
          key={item}
          item={item}
          hidden={item === 'blank' || (item === '<' && props.pinLength === 0)}
          onPress={props.onPress}
        />
      ))}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    width: '100%',
    height: '68%',
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  keyContainer: {
    minWidth: '30%',
    maxWidth: '32%',
    height: '24%',
    margin: 2,
    backgroundColor: '#ffffff',
    justifyContent: 'center',
    alignItems: 'center',
  },
  keyText: {
    fontSize: 28,
    fontWeight: '500',
    color: '#2aa4dd',
  },
});

export default PinKeyboard;
