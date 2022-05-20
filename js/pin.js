import { useState, useEffect } from "react";
import OneginiSdk, { ONEGINI_SDK_EVENTS } from "./index";

const ONEWELCOME_PIN_NOTIFICATIONS = {
  OPEN: 'open',
  CONFIRM: 'confirm',
  CLOSE: 'close',
  ERROR: 'show_error'
};

const ONEGINI_PIN_ACTIONS = {
  PROVIDE_PIN: 'provide',
  CANCEL: 'cancel',
};

const ONEGINI_PIN_FLOW = {
  AUTHENTICATION: 'authentication',
  CREATE: 'create',
  CHANGE: 'change',
};


const EventHandler = {
  listeners: {
    [ONEWELCOME_PIN_NOTIFICATIONS.OPEN]    : null,
    [ONEWELCOME_PIN_NOTIFICATIONS.CONFIRM] : null,
    [ONEWELCOME_PIN_NOTIFICATIONS.CLOSE]   : null,
    [ONEWELCOME_PIN_NOTIFICATIONS.ERROR]   : null,
  },

  registerPinOpenNotification: (cb) => EventHandler.listeners[ONEWELCOME_PIN_NOTIFICATIONS.OPEN] = cb,
  registerPinConfirmNotification: (cb) => EventHandler.listeners[ONEWELCOME_PIN_NOTIFICATIONS.CONFIRM] = cb,
  registerPinErrorNotification: (cb) => EventHandler.listeners[ONEWELCOME_PIN_NOTIFICATIONS.ERROR] = cb,
  registerPinCloseNotification: (cb) => EventHandler.listeners[ONEWELCOME_PIN_NOTIFICATIONS.CLOSE] = cb,

  deregisterPinOpenNotification: () => EventHandler.listeners[ONEWELCOME_PIN_NOTIFICATIONS.OPEN] = null,
  deregisterPinConfirmNotification: () => EventHandler.listeners[ONEWELCOME_PIN_NOTIFICATIONS.CONFIRM] = null,
  deregisterPinErrorNotification: () => EventHandler.listeners[ONEWELCOME_PIN_NOTIFICATIONS.ERROR] = null,
  deregisterPinCloseNotification: () => EventHandler.listeners[ONEWELCOME_PIN_NOTIFICATIONS.CLOSE] = null,

  handleNotificationEvent: (event) => {
    switch (event.action) {
      case ONEWELCOME_PIN_NOTIFICATIONS.OPEN:
        EventHandler.listeners[ONEWELCOME_PIN_NOTIFICATIONS.OPEN](event.flow);
        break;
      case ONEWELCOME_PIN_NOTIFICATIONS.CONFIRM:
        EventHandler.listeners[ONEWELCOME_PIN_NOTIFICATIONS.CONFIRM]();
        break;
      case ONEWELCOME_PIN_NOTIFICATIONS.ERROR:
        EventHandler.listeners[ONEWELCOME_PIN_NOTIFICATIONS.ERROR](event.errorMsg);
        break;
      case ONEWELCOME_PIN_NOTIFICATIONS.CLOSE:
        EventHandler.listeners[ONEWELCOME_PIN_NOTIFICATIONS.CLOSE]();
        break;
      default:
        console.log(
          'Got unsupported ONEWELCOME_PIN_NOTIFICATIONS action:',
          event.action,
        );
        break;
    }
  }
}
Object.freeze(EventHandler);


const onNewPinKey = (newKey, pin, setPin, flow, setError) => {
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
}

const onCancelPinFlow = (flow) => OneginiSdk.submitPinAction(flow, ONEGINI_PIN_ACTIONS.CANCEL, null);

const usePinFlow = () => {
  const [flow, setFlow] = useState(ONEGINI_PIN_FLOW.CREATE);
  const [pin, setPin] = useState('');
  const [visible, setVisible] = useState(false);
  const [error, setError] = useState(null);
  const [isConfirmMode, setConfirmMode] = useState(false);

  const setInitialState = () => {
    setError(null);
    setConfirmMode(false);
    setVisible(false);
    setPin('');
  };

  const setConfirmState = () => {
    setConfirmMode(true);
    setPin('');
  };

  const handleOpen = (newFlow) => {
    setVisible(true)
    if(flow !== newFlow) {
      setFlow(newFlow);
    }
  };

  const handleError = (err) => {
    setError(err);
    setConfirmMode(false);
    setPin('');
  }

  const provideNewPinKey = (newKey) => onNewPinKey(newKey, pin, setPin, flow, setError);
  const cancelPinFlow = () => onCancelPinFlow(flow);

  useEffect(() => {
    EventHandler.registerPinOpenNotification(handleOpen);
    EventHandler.registerPinConfirmNotification(setConfirmState);
    EventHandler.registerPinErrorNotification(handleError);
    EventHandler.registerPinCloseNotification(setInitialState);
    OneginiSdk.addEventListener(ONEGINI_SDK_EVENTS.ONEWELCOME_PIN_NOTIFICATION, EventHandler.handleNotificationEvent)
    return () => {
      OneginiSdk.removeEventListener(ONEGINI_SDK_EVENTS.ONEWELCOME_PIN_NOTIFICATION)
      EventHandler.deregisterPinOpenNotification();
      EventHandler.deregisterPinConfirmNotification();
      EventHandler.deregisterPinErrorNotification();
      EventHandler.deregisterPinCloseNotification();
    };
  }, []);

  return [
    flow,
    pin,
    visible,
    isConfirmMode,
    error,
    provideNewPinKey,
    cancelPinFlow,
  ];
}

export { usePinFlow, ONEGINI_PIN_FLOW }
