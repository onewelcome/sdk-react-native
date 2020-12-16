import React, {useState, useEffect} from 'react';
import {StyleSheet, View, Text, Platform} from 'react-native';
import PropTypes from 'prop-types';
import OneginiSdk, {
  ONEGINI_PIN_ACTIONS,
  ONEGINI_PIN_FLOW,
} from 'react-native-sdk-beta';
import PinInput from './PinInput';
import PinKeyboard from './PinKeyboard';
import Button from '../../general/Button';
import {handlePinEvent, onPinPress} from '../../helpers/PinHelpers';
import PinManager from '../../managers/PinManager';
import ObjectIDHelper from '../../helpers/ObjectIdHelper';

const PinView = (props) => {
  const [id, setId] = useState(ObjectIDHelper.getNewID('PinView'));
  const [isConfirmMode, setConfirmMode] = useState(false);
  const [error, setError] = useState(null);
  const [pin, setPin] = useState('');

  useEffect(() => {
    PinManager.addEventListener(id, (event) => {
      handlePinEvent(event, props.setVisible, setPin, setConfirmMode, setError);
    });

    return () => {
      PinManager.removeEventListener(id);
    };
  }, [id, props.setVisible]);

  function getTitle() {
    if (props.flow === ONEGINI_PIN_FLOW.AUTHENTICATION) {
      return 'Current Pin';
    } else if (props.flow === ONEGINI_PIN_FLOW.CREATE) {
      return 'Create Pin';
    } else if (props.flow === ONEGINI_PIN_FLOW.CHANGE) {
      return 'Change Pin';
    } else {
      return '';
    }
  }

  return (
    <View style={styles.container}>
      <View style={styles.topContainer}>
        <Text style={styles.title}>
          {isConfirmMode ? 'Confirm Pin' : getTitle()}
        </Text>
        <PinInput pinLength={pin.length} />
        {error && <Text style={styles.error}>{error}</Text>}
      </View>
      <View style={styles.bottomContainer}>
        <PinKeyboard
          pinLength={pin.length}
          onPress={(newKey) =>
            onPinPress(props.flow, newKey, pin, setPin, setError)
          }
        />
        <Button
          name={'Cancel'}
          containerStyle={styles.buttonStyle}
          onPress={() => OneginiSdk.submitPinAction(
              props.flow,
              ONEGINI_PIN_ACTIONS.CANCEL,
              null,
            )}
        />
      </View>
    </View>
  );
};

PinView.propTypes = {
  flow: PropTypes.string,
  setVisible: PropTypes.func.isRequired,
};

const styles = StyleSheet.create({
  container: {
    width: '100%',
    height: '100%',
    backgroundColor: '#cacaca',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 20,
  },
  topContainer: {
    flex: 1,
    alignItems: 'center',
  },
  title: {
    fontSize: 32,
    marginTop: '10%',
  },
  error: {
    marginTop: '8%',
    fontSize: 22,
    color: '#c82d2d',
  },
  bottomContainer: {
    flex: 1,
    width: '100%',
    alignItems: 'center',
    justifyContent: 'flex-end',
    paddingBottom: 20,
  },
  buttonStyle: {
    backgroundColor: '#2aa4dd',
    width: '80%',
    marginTop: 20,
  },
});

export default PinView;
