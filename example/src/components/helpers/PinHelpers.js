import OneginiSdk, {
  ONEGINI_PIN_ACTIONS,
  ONEGINI_PIN_NOTIFICATIONS,
  ONEGINI_PIN_FLOW,
} from 'react-native-sdk-beta';

const onPinPress = (flow, newKey, pin, setPin, setError) => {
  setError(null);

  if (newKey === '<' && pin.length > 0) {
    setPin(pin.substring(0, pin.length - 1));
  } else {
    const newValue = pin + newKey;
    setPin(newValue);
    if (newValue.length === 5) {
      OneginiSdk.submitPinAction(
        flow,
        ONEGINI_PIN_ACTIONS.PROVIDE_PIN,
        newValue,
      );
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
