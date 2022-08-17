export type SdkEvent =
  | PinEvent
  | CustomRegistrationEvent
  | FingerprintEvent
  | MobileAuthOtpEvent;

// Pin
export type PinEvent =
  | PinCloseEvent
  | PinChangedEvent
  | PinConfirmEvent
  | PinOpenEvent
  | PinErrorEvent;

export type PinCloseEvent = {
  action: Pin.Close;
  flow: PinFlow;
};

export type PinChangedEvent = {
  action: Pin.Changed;
  flow: PinFlow;
};

// TODO: Remove this, it is only here so it compiles but will be removed when the SDK/example app remove this event.
export type PinConfirmEvent = {
  action: Pin.Confirm;
  flow: PinFlow;
};

export type PinOpenEvent = {
  action: Pin.Open;
  flow: PinFlow;
  profileId: string;
  data?: number; //pin length
};

export type PinErrorEvent = {
  action: Pin.Error;
  flow: PinFlow;
  errorType: number;
  errorMsg: string;
  userInfo?: {
    remainingFailureCount: string;
  };
};

//CustomRegistration
export type CustomRegistrationEvent =
  | initRegistrationEvent
  | finishRegistrationEvent;

export type initRegistrationEvent = {
  action: CustomRegistration.InitRegistration;
  identityProviderId: string;
  customInfo?: {
    data: string;
    status: number;
  };
};

export type finishRegistrationEvent = {
  action: CustomRegistration.FinishRegistration;
  identityProviderId: string;
  customInfo?: {
    data: string;
    status: number;
  };
};

//MobileAuthOtp
export type MobileAuthOtpEvent =
  | startOtpAuthenticationEvent
  | finishOtpAuthenticationEvent;

export type startOtpAuthenticationEvent = {
  action: MobileAuthOtp.StartAuthentication;
  mobileAuthenticationRequest: {
    message: string;
    type: string;
    transactionId: string;
    signingData: string;
  };
};

export type finishOtpAuthenticationEvent = {
  action: MobileAuthOtp.FinishAuthentication;
  mobileAuthenticationRequest: {
    message: string;
    type: string;
    transactionId: string;
    signingData: string;
  };
};

//FingerPrint
export type FingerprintEvent =
  | startFingerprintAuthenticationEvent
  | onNextFingerprintAuthenticationAttemptEvent
  | onFingerprintCapturedEvent
  | finishFingerprintAuthenticationEvent;
export type startFingerprintAuthenticationEvent = {
  action: Fingerprint.StartAuthentication;
  userProfile: {
    profileId: string;
  };
};
export type onNextFingerprintAuthenticationAttemptEvent = {
  action: Fingerprint.OnNextAuthenticationAttempt;
};
export type onFingerprintCapturedEvent = {
  action: Fingerprint.OnFingerprintCaptured;
};
export type finishFingerprintAuthenticationEvent = {
  action: Fingerprint.FinishAuthentication;
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

export enum Pin {
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

export enum CustomRegistration {
  InitRegistration = 'initRegistration',
  FinishRegistration = 'finishRegistration',
}

export enum Fingerprint {
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

export enum MobileAuthOtp {
  StartAuthentication = 'startAuthentication',
  FinishAuthentication = 'finishAuthentication',
}
