import React from 'react';
import {StyleSheet, Text, Pressable} from 'react-native';
import PropTypes from 'prop-types';

const Button = (props) => {
  const buttonStyle = {
    ...styles.button,
    ...props.containerStyle,
    opacity: props.disabled ? 0.5 : 1,
  };

  return (
    <Pressable
      style={({pressed}) => [
        {
          backgroundColor: pressed
            ? props.backgroundColorPressed
            : props.backgroundColor,
        },
        buttonStyle,
      ]}
      disabled={props.disabled}
      onPress={() => (props.disabled ? null : props.onPress())}>
      <Text style={styles.text}>{props.name}</Text>
    </Pressable>
  );
};

Button.propTypes = {
  name: PropTypes.string.isRequired,
  onPress: PropTypes.func.isRequired,
  disabled: PropTypes.bool,
  backgroundColor: PropTypes.string,
  backgroundColorPressed: PropTypes.string,
};

Button.defaultProps = {
  disabled: false,
  backgroundColor: '#25b2ff',
  backgroundColorPressed: '#1e8dca',
};

const styles = StyleSheet.create({
  button: {
    width: '100%',
    padding: 15,
    alignItems: 'center',
    justifyContent: 'center',
    borderRadius: 3,
  },
  text: {
    fontSize: 18,
    fontWeight: '400',
    color: '#ffffff',
  },
});

export default Button;
