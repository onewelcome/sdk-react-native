export type SdkNotificationEvent =
  | PinNotificationEvent
  | CustomRegistrationNotificationEvent
  | FingerprintNotificationEvent
  | MobileAuthOtpNotificationEvent;

// PinNotification
export type PinNotificationEvent =
  | PinNotificationCloseEvent
  | PinNotificationChangedEvent
  | PinNotificationConfirmEvent
  | PinNotificationOpenEvent
  | PinNotificationErrorEvent;

export type PinNotificationCloseEvent = {
  action: PinNotification.Close;
  flow: PinFlow;
};

export type PinNotificationChangedEvent = {
  action: PinNotification.Changed;
  flow: PinFlow;
};

// TODO: Remove this, it is only here so it compiles but will be removed when the SDK/example app remove this event.
export type PinNotificationConfirmEvent = {
  action: PinNotification.Confirm;
  flow: PinFlow;
};

export type PinNotificationOpenEvent = {
  action: PinNotification.Open;
  flow: PinFlow;
  profileId: string;
  data?: number; //pin length
};

export type PinNotificationErrorEvent = {
  action: PinNotification.Error;
  flow: PinFlow;
  errorType: number;
  errorMsg: string;
  userInfo?: {
    remainingFailureCount: string;
  };
};

//CustomRegistrationNotification
export type CustomRegistrationNotificationEvent =
  | initRegistrationEvent
  | finishRegistrationEvent;

export type initRegistrationEvent = {
  action: CustomRegistrationNotification;
  identityProviderId: string;
  customInfo?: {
    data: string;
    status: number;
  };
};

export type finishRegistrationEvent = {
  action: CustomRegistrationNotification;
  identityProviderId: string;
  customInfo?: {
    data: string;
    status: number;
  };
};

//MobileAuthOtpNotification
export type MobileAuthOtpNotificationEvent =
  | startOtpAuthenticationEvent
  | finishOtpAuthenticationEvent;

export type startOtpAuthenticationEvent = {
  action: MobileAuthOtpNotification.StartAuthentication;
  mobileAuthenticationRequest: {
    message: string;
    type: string;
    transactionId: string;
    signingData: string;
  };
};

export type finishOtpAuthenticationEvent = {
  action: MobileAuthOtpNotification.FinishAuthentication;
  mobileAuthenticationRequest: {
    message: string;
    type: string;
    transactionId: string;
    signingData: string;
  };
};

//FingerPrintNotification
export type FingerprintNotificationEvent =
  | startFingerprintAuthenticationEvent
  | onNextFingerprintAuthenticationAttemptEvent
  | onFingerprintCapturedEvent
  | finishFingerprintAuthenticationEvent;
export type startFingerprintAuthenticationEvent = {
  action: FingerprintNotification.StartAuthentication;
  userProfile: {
    profileId: string;
  };
};
export type onNextFingerprintAuthenticationAttemptEvent = {
  action: FingerprintNotification.OnNextAuthenticationAttempt;
};
export type onFingerprintCapturedEvent = {
  action: FingerprintNotification.OnFingerprintCaptured;
};
export type finishFingerprintAuthenticationEvent = {
  action: FingerprintNotification.FinishAuthentication;
};

export enum SdkNotification {
  Pin = 'ONEWELCOME_PIN_NOTIFICATION',
  CustomRegistration = 'ONEWELCOME_CUSTOM_REGISTRATION_NOTIFICATION',
  MobileAuthOtp = 'ONEWELCOME_MOBILE_AUTH_OTP_NOTIFICATION',
  Fingerprint = 'ONEWELCOME_FINGERPRINT_NOTIFICATION',
}

export enum PinAction {
  ProvidePin = 'provide',
  Cancel = 'cancel',
}

export enum PinFlow {
  Authentication = 'authentication',
  Create = 'create',
  Change = 'change',
}

export enum PinNotification {
  Open = 'open',
  Confirm = 'confirm',
  Close = 'close',
  Error = 'show_error',
  Changed = 'changed',
}

export enum CustomRegistrationAction {
  ProvideToken = 'provide',
  Cancel = 'cancel',
}

export enum CustomRegistrationNotification {
  InitRegistration = 'initRegistration',
  FinishRegistration = 'finishRegistration',
}

export enum FingerprintNotification {
  StartAuthentication = 'startAuthentication',
  OnNextAuthenticationAttempt = 'onNextAuthenticationAttempt',
  OnFingerprintCaptured = 'onFingerprintCaptured',
  FinishAuthentication = 'finishAuthentication',
}

export enum FingerprintStage {
  Idle = 'idle',
  Started = 'started',
  NextAttempt = 'nextAttempt',
  Captured = 'captured',
  Finished = 'finished',
}

export enum MobileAuthOtpNotification {
  StartAuthentication = 'startAuthentication',
  FinishAuthentication = 'finishAuthentication',
}
