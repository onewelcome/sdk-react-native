//
// Not used?
//

import React from 'react';

/*
import OneginiSdk, {
  PinActions,
  PinFlow,
  PinNotifications,
} from 'react-native-sdk-beta';


const onPinPress = (flow: PinFlow, newKey, pin, setPin, resetError) => {
  resetError();

  if (newKey === '<' && pin.length > 0) {
    setPin(pin.substring(0, pin.length - 1));
  } else {
    const newValue = pin + newKey;
    setPin(newValue);
    if (newValue.length === 5) {
      OneginiSdk.submitPinAction(flow, PinActions.ProvidePin, newValue);
    }
  }
};

const handlePinEvent = (
  event,
  setVisiblity,
  setPin,
  setConfirmMode,
  setError,
) => {
  switch (event.action) {
    case ONEGINI_PIN_NOTIFICATIONS.OPEN:
      setVisiblity(true);
      break;
    case ONEGINI_PIN_NOTIFICATIONS.CONFIRM:
      setPin('');
      setConfirmMode(true);
      break;
    case ONEGINI_PIN_NOTIFICATIONS.ERROR:
      setPin('');
      setConfirmMode(false);
      setError(event.errorMsg);
      break;
    case ONEGINI_PIN_NOTIFICATIONS.CLOSE:
      setError(null);
      setConfirmMode(false);
      setPin('');
      setVisiblity(false);
      break;
    default:
      console.log(
        'Got unsupported ONEGINI_PIN_NOTIFICATIONS action:',
        event.action,
      );
      break;
  }
};

export {onPinPress, handlePinEvent};
*/
