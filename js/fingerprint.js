import { useState, useEffect } from "react";
import { Platform } from 'react-native';
import OneginiSdk, { ONEGINI_SDK_EVENTS } from "./index";

const ONEGINI_FINGERPRINT_STAGE = {
  IDLE: 'idle',
  STARTED: 'started',
  NEXT_ATTEMPT: 'nextAttempt',
  CAPTURED: 'captured',
  FINISHED: 'finished',
};

const ONEWELCOME_FINGERPRINT_NOTIFICATIONS = {
  START_AUTH : "startAuthentication",
  ON_NEXT_AUTH_ATTEMPT : "onNextAuthenticationAttempt",
  ON_CAPTURED : "onFingerprintCaptured",
  FINISH_AUTH : "finishAuthentication"
};

const FingerprintEventHandler = {
  listeners: {
    [ONEWELCOME_FINGERPRINT_NOTIFICATIONS.START_AUTH]: null,
    [ONEWELCOME_FINGERPRINT_NOTIFICATIONS.ON_NEXT_AUTH_ATTEMPT]: null,
    [ONEWELCOME_FINGERPRINT_NOTIFICATIONS.ON_CAPTURED]: null,
    [ONEWELCOME_FINGERPRINT_NOTIFICATIONS.FINISH_AUTH]: null,
  },

  registerStartAuthNotification: (cb) =>
    FingerprintEventHandler.listeners[ONEWELCOME_FINGERPRINT_NOTIFICATIONS.START_AUTH] = cb,
  registerOnNextAuthAttemptNotification: (cb) =>
    FingerprintEventHandler.listeners[ONEWELCOME_FINGERPRINT_NOTIFICATIONS.ON_NEXT_AUTH_ATTEMPT] = cb,
  registerOnCapturedNotification: (cb) =>
    FingerprintEventHandler.listeners[ONEWELCOME_FINGERPRINT_NOTIFICATIONS.ON_CAPTURED] = cb,
  registerFinishAuthNotification: (cb) =>
    FingerprintEventHandler.listeners[ONEWELCOME_FINGERPRINT_NOTIFICATIONS.FINISH_AUTH] = cb,

  deregisterStartAuthNotification: () =>
    FingerprintEventHandler.listeners[ONEWELCOME_FINGERPRINT_NOTIFICATIONS.START_AUTH] = null,
  deregisterOnNextAuthAttemptNotification: () =>
    FingerprintEventHandler.listeners[ONEWELCOME_FINGERPRINT_NOTIFICATIONS.ON_NEXT_AUTH_ATTEMPT] = null,
  deregisterOnCapturedNotification: () =>
    FingerprintEventHandler.listeners[ONEWELCOME_FINGERPRINT_NOTIFICATIONS.ON_CAPTURED] = null,
  deregisterFinishAuthNotification: () =>
    FingerprintEventHandler.listeners[ONEWELCOME_FINGERPRINT_NOTIFICATIONS.FINISH_AUTH] = null,

  handleNotificationEvent: (event) => {
    switch (event.action) {
      case ONEWELCOME_FINGERPRINT_NOTIFICATIONS.START_AUTH:
        FingerprintEventHandler.listeners[ONEWELCOME_FINGERPRINT_NOTIFICATIONS.START_AUTH]();
        break;
      case ONEWELCOME_FINGERPRINT_NOTIFICATIONS.ON_NEXT_AUTH_ATTEMPT:
        FingerprintEventHandler.listeners[ONEWELCOME_FINGERPRINT_NOTIFICATIONS.ON_NEXT_AUTH_ATTEMPT]();
        break;
      case ONEWELCOME_FINGERPRINT_NOTIFICATIONS.ON_CAPTURED:
        FingerprintEventHandler.listeners[ONEWELCOME_FINGERPRINT_NOTIFICATIONS.ON_CAPTURED]();
        break;
      case ONEWELCOME_FINGERPRINT_NOTIFICATIONS.FINISH_AUTH:
        FingerprintEventHandler.listeners[ONEWELCOME_FINGERPRINT_NOTIFICATIONS.FINISH_AUTH]();
        break;
      default:
        console.log(
          'Got unsupported ONEWELCOME_FINGERPRINT_NOTIFICATIONS action:',
          event.action,
        );
        break;
    }
  }
}
Object.freeze(FingerprintEventHandler);


const onCancelFlow = () => Platform.OS === 'android'
  ? OneginiSdk.submitFingerprintDenyAuthenticationRequest()
  : null; // iOS handled natively

const onFallbackToPin = () => Platform.OS === 'android'
  ? OneginiSdk.submitFingerprintFallbackToPin()
  : null; // iOS handled natively

const useFingerprintFlow = () => {
  const [active, setActive] = useState(false);
  const [stage, setStage] = useState(ONEGINI_FINGERPRINT_STAGE.IDLE);

  const onStart = () => {
    setStage(ONEGINI_FINGERPRINT_STAGE.STARTED);
    setActive(true);
    OneginiSdk.submitFingerprintAcceptAuthenticationRequest();
  };
  const cancelFlow = () => {
    setActive(false);
    setStage(ONEGINI_FINGERPRINT_STAGE.IDLE)
    onCancelFlow();
  };
  const fallbackToPin = () => {
    setActive(false);
    setStage(ONEGINI_FINGERPRINT_STAGE.IDLE)
    onFallbackToPin();
  }
  const onFinish = () => {
    setActive(false);
    setStage(ONEGINI_FINGERPRINT_STAGE.FINISHED);
  }
  const onNextAuthAttempt = () => setStage(ONEGINI_FINGERPRINT_STAGE.NEXT_ATTEMPT);
  const onCaptured = () => setStage(ONEGINI_FINGERPRINT_STAGE.CAPTURED);


  useEffect(() => {
    FingerprintEventHandler.registerStartAuthNotification(onStart);
    FingerprintEventHandler.registerOnNextAuthAttemptNotification(onNextAuthAttempt);
    FingerprintEventHandler.registerOnCapturedNotification(onCaptured);
    FingerprintEventHandler.registerFinishAuthNotification(onFinish);
    OneginiSdk.addEventListener(ONEGINI_SDK_EVENTS.ONEWELCOME_FINGERPRINT_NOTIFICATION, FingerprintEventHandler.handleNotificationEvent)
    return () => {
      OneginiSdk.removeEventListener(ONEGINI_SDK_EVENTS.ONEWELCOME_FINGERPRINT_NOTIFICATION)
      FingerprintEventHandler.deregisterStartAuthNotification();
      FingerprintEventHandler.deregisterOnNextAuthAttemptNotification();
      FingerprintEventHandler.deregisterOnCapturedNotification();
      FingerprintEventHandler.deregisterFinishAuthNotification();
    };
  }, []);

  return [
    active,
    stage,
    fallbackToPin,
    cancelFlow,
  ];
}

export { useFingerprintFlow, ONEGINI_FINGERPRINT_STAGE }
