import {useCallback, useEffect, useState} from 'react';
import OnewelcomeSdk, {Events} from './index';
import {useProfileStorage} from "../example/src/components/hooks/useProfileStorage";

const usePinFlow = () => {
  const [flow, setFlow] = useState<Events.PinFlow>(Events.PinFlow.Create);
  const [pin, setPin] = useState('');
  const [firstPin, setFirstPin] = useState('');
  const [visible, setVisible] = useState(false);
  const [userInfo, setUserInfo] = useState(null);
  const [error, setError] = useState<string | null>(null);
  const [isConfirmMode, setConfirmMode] = useState(false);
  const [pinLength, setPinLength] = useState<number | null>(null);
  const {getPinProfile, setPinProfile} = useProfileStorage();

  const provideNewPinKey = (newKey: string) =>
    onNewPinKey(newKey, pin, setPin, flow, setError, pinLength || 5, isConfirmMode, setConfirmMode, firstPin, setFirstPin);

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
    async (newFlow: Events.PinFlow, profileId: string, pinLength?: number) => {
      setVisible(true);
      if (flow !== newFlow) {
        setFlow(newFlow);
      }
      if(pinLength && !isNaN(Number(pinLength))){
        await setPinProfile(profileId, pinLength);
      } else {
        pinLength = await getPinProfile(profileId);
      }
      setPinLength(pinLength);
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
    async (event: any) => {
      console.log('handle PIN notification event: ', event);

      switch (event.action) {
        case Events.PinNotification.Open:
          await handleOpen(event.flow, event.profileId, event.data);
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
    const listener = OnewelcomeSdk.addEventListener(
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

const onNewPinKey = (
  newKey: string,
  pin: string,
  setPin: (pin: string) => void,
  flow: Events.PinFlow,
  setError: (error: string | null) => void,
  requiredPinLength: number,
  isConfirmMode: boolean,
  setConfirmMode: (mode: boolean) => void,
  firstPin: string,
  setFirstPin: (pin: string) => void
) => {
  setError(null);
  if (newKey === '<' && pin.length > 0) {
    setPin(pin.substring(0, pin.length - 1));
    return;
  }
  const newValue = pin + newKey;
  setPin(newValue);
  switch (flow) {
    case Events.PinFlow.Authentication:
      if (newValue.length === requiredPinLength) {
          OnewelcomeSdk.submitPinAction(flow, Events.PinAction.ProvidePin, newValue);
      }
      break;
    case Events.PinFlow.Create:
    case Events.PinFlow.Change:
      if (isConfirmMode) {
        handleConfirmPin();
      } else {
        handleFirstPin();
      }
      break;
  }
  function handleConfirmPin() {
    if (newValue.length === requiredPinLength) {
      if (firstPin === newValue) {
        OnewelcomeSdk.submitPinAction(flow, Events.PinAction.ProvidePin, newValue);
      } else {
        setError('Pins do not match');
        setConfirmMode(false);
        setPin('');
      }
    }
  }
  function handleFirstPin() {
    setFirstPin(newValue);
    if (newValue.length === requiredPinLength) {
      setConfirmMode(true);
      setPin('');
    }
  }
};

const onCancelPinFlow = (flow: Events.PinFlow) =>
  OnewelcomeSdk.submitPinAction(flow, Events.PinAction.Cancel, null);

export {usePinFlow};
