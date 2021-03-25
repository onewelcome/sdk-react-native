import React from 'react';
import {StyleSheet, Image, Pressable, Platform, ViewStyle} from 'react-native';
import {Assets} from '../../../assets';

interface Props {
  onPress?: () => void;
  disabled?: boolean;
  tintColor?: string;
  tintColorPressed?: string;
  containerStyle?: ViewStyle;
}

const BackButton: React.FC<Props> = ({
  disabled = false,
  tintColor = '#ffffff',
  tintColorPressed = '#c8c8c8',
  onPress,
  containerStyle,
}) => {
  const buttonStyle = [
    {
      ...styles.button,
      ...containerStyle,
      opacity: disabled ? 0.5 : 1,
    },
  ];

  return (
    <Pressable
      style={buttonStyle}
      disabled={disabled}
      onPress={() => (disabled ? null : onPress?.())}>
      {({pressed}) => (
        <Image
          source={
            Platform.OS === 'android' ? Assets.backAndroid : Assets.backIos
          }
          style={[
            styles.icon,
            {tintColor: pressed ? tintColorPressed : tintColor},
          ]}
        />
      )}
    </Pressable>
  );
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
