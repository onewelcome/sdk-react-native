import React from 'react';
import {View, StyleSheet, Text, Switch as RNSwitch} from 'react-native';
import PropTypes from 'prop-types';

const Switch = (props) => {
  const containerStyle = {
    ...styles.container,
    ...props.containerStyle,
  };

  const labelStyle = {
    ...styles.label,
    ...props.labelStyle,
  };

  return (
    <View style={containerStyle}>
      <Text style={labelStyle}>{props.label}</Text>
      <RNSwitch
        style={styles.switch}
        trackColor={{false: '#c6c6c6', true: '#9dddff'}}
        thumbColor={props.value ? '#11aeff' : '#f4f3f4'}
        ios_backgroundColor="#c6c6c6"
        onValueChange={props.onSwitch}
        value={props.value}
        disabled={props.disabled}
      />
    </View>
  );
};

Switch.propTypes = {
  value: PropTypes.bool.isRequired,
  onSwitch: PropTypes.func.isRequired,
  label: PropTypes.string.isRequired,
  disabled: PropTypes.bool,
  backgroundColor: PropTypes.string,
  backgroundColorPressed: PropTypes.string,
};

Switch.defaultProps = {
  disabled: false,
  backgroundColor: '#25b2ff',
  backgroundColorPressed: '#1e8dca',
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
