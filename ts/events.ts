export type SdkEvent =
  | PinCreateEvent
  | PinAuthenticationEvent
  | CustomRegistrationEvent
  | FingerprintEvent
  | MobileAuthOtpEvent
  | RegistrationURLEvent;

// Pin
export type PinCreateEvent =
  | PinCreateCloseEvent
  | PinCreateOpenEvent
  | PinNotAllowedEvent;

export type PinAuthenticationEvent =
  | PinAuthenticationCloseEvent
  | PinAuthenticationOpenEvent
  | IncorrectPinEvent;

// Create Pin
export type PinCreateCloseEvent = {
  action: PinCreate.Close;
  flow: PinFlow.Create;
};

export type PinCreateOpenEvent = {
  flow: PinFlow.Create;
  action: PinCreate.Open;
  profileId: string;
  pinLength: number;
};

export type PinNotAllowedEvent = {
  flow: PinFlow.Create;
  action: PinCreate.PinNotAllowed;
  errorType: number;
  errorMsg: string;
};

// Authentication Pin

export type PinAuthenticationCloseEvent = {
  action: PinAuthentication.Close;
  flow: PinFlow.Authentication;
};

export type PinAuthenticationOpenEvent = {
  flow: PinFlow.Authentication;
  action: PinAuthentication.Open;
  profileId: string;
};

export type IncorrectPinEvent = {
  flow: PinFlow.Authentication;
  action: PinAuthentication.IncorrectPin;
  errorType: number;
  errorMsg: string;
  remainingFailureCount: number;
};
//Registration
export type RegistrationURLEvent = {
  url: string;
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
  PinCreate = 'ONEWELCOME_PIN_CREATE_NOTIFICATION',
  PinAuth = 'ONEWELCOME_PIN_AUTHENTICATION_NOTIFICATION',
  CustomRegistration = 'ONEWELCOME_CUSTOM_REGISTRATION_NOTIFICATION',
  MobileAuthOtp = 'ONEWELCOME_MOBILE_AUTH_OTP_NOTIFICATION',
  Fingerprint = 'ONEWELCOME_FINGERPRINT_NOTIFICATION',
  Registration = 'ONEWELCOME_REGISTRATION_NOTIFICATION',
}

export enum PinFlow {
  Authentication = 'Authentication',
  Create = 'Create',
  Change = 'Change',
}

export enum PinCreate {
  Open = 'open',
  Close = 'close',
  PinNotAllowed = 'pinNotAllowed',
}

export enum PinAuthentication {
  Open = 'open',
  Close = 'close',
  IncorrectPin = 'incorrectPin',
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

//TOOD: Add more error codes here.
export enum PinErrorCode {
  WrongPinErrorCode = 8004,
}
