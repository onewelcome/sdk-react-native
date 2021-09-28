import React from 'react';
import {StyleSheet, View, ViewStyle} from 'react-native';

interface Props {
  containerStyle?: ViewStyle;
}

const ContentContainer: React.FC<Props> = (props) => {
  return (
    <View style={[styles.container, props.containerStyle]}>
      {props.children}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    position: 'absolute',
    alignSelf: 'center',
    top: '10%',
    width: '100%',
    height: '100%',
    borderRadius: 4,
    backgroundColor: '#ffffff',
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 1,
    },
    shadowOpacity: 0.2,
    shadowRadius: 2,
    elevation: 3,
    paddingHorizontal: '12%',
    paddingTop: '18%',
  },
});

export default ContentContainer;
