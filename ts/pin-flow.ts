import {useCallback, useEffect, useState} from 'react';
import OneginiSdk, {Events} from './index';
import {PinAction, PinFlow} from "./events";

const usePinFlow = () => {
  const [flow, setFlow] = useState<Events.PinFlow>(Events.PinFlow.Create);
  const [pin, setPin] = useState('');
  const [visible, setVisible] = useState(false);
  const [userInfo, setUserInfo] = useState(null);
  const [error, setError] = useState<string | null>(null);
  const [isConfirmMode, setConfirmMode] = useState(false);
  const [pinLength, setPinLength] = useState<number | null>(null);

  const provideNewPinKey = (newKey: string) =>
    onNewPinKey(newKey, pin, setPin, flow, setError, pinLength || 5);

  const cancelPinFlow = () => onCancelPinFlow(flow);

  // Event Handlers

  const setInitialState = useCallback(() => {
    setError(null);
    setConfirmMode(false);
    setVisible(false);
    setPin('');
  }, []);

  const setConfirmState = useCallback(() => {
    setConfirmMode(true);
    setPin('');
  }, []);

  const handleOpen = useCallback(
    (newFlow: Events.PinFlow, pinLength?: number) => {
      setVisible(true);
      if (flow !== newFlow) {
        setFlow(newFlow);
      }
      if(pinLength){
        setPinLength(pinLength);
      }
    },
    [flow],
  );

  const handleError = useCallback((err: string | null, userInfo?: any) => {
    setError(err);
    setConfirmMode(false);
    setUserInfo(userInfo || null);
    setPin('');
  }, []);

  const handleNotification = useCallback(
    (event: any) => {
      console.log('handle PIN notification event: ', event);

      switch (event.action) {
        case Events.PinNotification.Open:
          handleOpen(event.flow, event.data);
          break;
        case Events.PinNotification.Confirm:
          setConfirmState();
          break;
        case Events.PinNotification.Error:
          handleError(event.errorMsg, event.userInfo ?? undefined);
          break;
        case Events.PinNotification.Close:
          setInitialState();
          break;
      }
    },
    [handleOpen, setConfirmState, handleError, setInitialState],
  );

  useEffect(() => {
    const listener = OneginiSdk.addEventListener(
      Events.SdkNotification.Pin,
      handleNotification,
    );

    return () => {
      listener.remove();
    };
  }, [handleNotification]);

  return {
    flow,
    pin,
    visible,
    isConfirmMode,
    error,
    provideNewPinKey,
    cancelPinFlow,
    pinLength,
    userInfo,
  };
};

//

const onNewPinKey = (
  newKey: string,
  pin: string,
  setPin: (pin: string) => void,
  flow: Events.PinFlow,
  setError: (error: string | null) => void,
  requiredPinLength: number,
) => {
  setError(null);

  if (newKey === '<' && pin.length > 0) {
    setPin(pin.substring(0, pin.length - 1));
  } else {
    const newValue = pin + newKey;
    setPin(newValue);
    if (newValue.length === requiredPinLength) {
      OneginiSdk.submitPinAction(flow, Events.PinAction.ProvidePin, newValue);
    }
  }
};

const onCancelPinFlow = (flow: Events.PinFlow) =>
  OneginiSdk.submitPinAction(flow, Events.PinAction.Cancel, null);

export {usePinFlow};
