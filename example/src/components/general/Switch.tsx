import React from 'react';
import {
  View,
  StyleSheet,
  Text,
  Switch as RNSwitch,
  ViewStyle,
  TextStyle,
} from 'react-native';

interface Props {
  value: boolean;
  onSwitch?: (enabled: boolean) => void;
  label: string;
  disabled?: boolean;
  backgroundColor?: string;
  backgroundColorPressed?: string;
  containerStyle?: ViewStyle;
  labelStyle?: ViewStyle | TextStyle;
}

const Switch: React.FC<Props> = ({
  value,
  onSwitch,
  label,
  disabled = false,
  backgroundColor = '#25b2ff',
  backgroundColorPressed = '#1e8dca',
  containerStyle,
  labelStyle,
}) => {
  const containerStyles = {
    ...styles.container,
    ...containerStyle,
  };

  const labelStyles = {
    ...styles.label,
    ...labelStyle,
  };

  return (
    <View style={containerStyles}>
      <Text style={labelStyles}>{label}</Text>
      <RNSwitch
        style={styles.switch}
        trackColor={{false: '#c6c6c6', true: '#9dddff'}}
        thumbColor={value ? '#11aeff' : '#f4f3f4'}
        ios_backgroundColor="#c6c6c6"
        onValueChange={onSwitch}
        value={value}
        disabled={disabled}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    width: '100%',
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  label: {
    fontSize: 15,
    fontWeight: '400',
    color: '#777777',
  },
  switch: {
    transform: [{scale: 0.7}],
  },
});

export default Switch;
