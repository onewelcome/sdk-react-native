import React from 'react';
import {StyleSheet, Image, Pressable, Platform} from 'react-native';
import PropTypes from 'prop-types';
import {Assets} from '../../../assets';

const BackButton = (props) => {
  const buttonStyle = {
    ...styles.button,
    ...props.containerStyle,
    opacity: props.disabled ? 0.5 : 1,
  };

  return (
    <Pressable
      style={buttonStyle}
      disabled={props.disabled}
      onPress={() => (props.disabled ? null : props.onPress())}>
      {({pressed}) => (
        <Image
          source={
            Platform.OS === 'android' ? Assets.backAndroid : Assets.backIos
          }
          style={[
            styles.icon,
            {tintColor: pressed ? props.tintColorPressed : props.tintColor},
          ]}
        />
      )}
    </Pressable>
  );
};

BackButton.propTypes = {
  onPress: PropTypes.func.isRequired,
  disabled: PropTypes.bool,
  tintColor: PropTypes.string,
  tintColorPressed: PropTypes.string,
};

BackButton.defaultProps = {
  disabled: false,
  tintColor: '#ffffff',
  tintColorPressed: '#c8c8c8',
};

const styles = StyleSheet.create({
  button: {
    position: 'absolute',
    left: '1%',
    flexDirection: 'row',
    alignItems: 'center',
    width: '12%',
    height: '80%',
    paddingLeft: '20%',
  },
  icon: {
    width: '50%',
    height: '40%',
    resizeMode: 'contain',
  },
});

export default BackButton;
