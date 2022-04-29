import { useState, useEffect, useCallback } from 'react';
import { Platform } from 'react-native';
import OnewelcomeSdk, {Events} from './index';

const onCancelFlow = () => Platform.OS === 'android'
  ? OnewelcomeSdk.submitFingerprintDenyAuthenticationRequest()
  : null; // iOS handled natively

const onFallbackToPin = () => Platform.OS === 'android'
  ? OnewelcomeSdk.submitFingerprintFallbackToPin()
  : null; // iOS handled natively

const useFingerprintFlow = () => {
  const [active, setActive] = useState<boolean>(false);
  const [stage, setStage] = useState<Events.FingerprintStage>(Events.FingerprintStage.Idle);

  const onStart = () => {
    setStage(Events.FingerprintStage.Started);
    setActive(true);
    OnewelcomeSdk.submitFingerprintAcceptAuthenticationRequest();
  };
  const cancelFlow = () => {
    setActive(false);
    setStage(Events.FingerprintStage.Idle)
    onCancelFlow();
  };
  const fallbackToPin = () => {
    setActive(false);
    setStage(Events.FingerprintStage.Idle)
    onFallbackToPin();
  }
  const onFinish = () => {
    setActive(false);
    setStage(Events.FingerprintStage.Finished);
  }
  const onNextAuthAttempt = () => setStage(Events.FingerprintStage.NextAttempt);
  const onCaptured = () => setStage(Events.FingerprintStage.Captured);

  const handleNotification = useCallback(
    (event: any) => {
      console.log('handle FINGERPRINT notification event: ', event);

      switch (event.action) {
        case Events.FingerprintNotification.StartAuthentication:
          onStart()
          break;
        case Events.FingerprintNotification.OnNextAuthenticationAttempt:
          onNextAuthAttempt();
          break;
        case Events.FingerprintNotification.OnFingerprintCaptured:
          onCaptured()
          break;
        case Events.FingerprintNotification.FinishAuthentication:
          onFinish();
          break;
      }
    },
    [onStart, onNextAuthAttempt, onCaptured, onFinish],
  );

  useEffect(() => {
    const listener = OnewelcomeSdk.addEventListener(
      Events.SdkNotification.Fingerprint,
      handleNotification,
    );

    return () => {
      listener.remove();
    };
  }, [handleNotification]);

  return {
    active,
    stage,
    fallbackToPin,
    cancelFlow,
  };
}

export {useFingerprintFlow}
