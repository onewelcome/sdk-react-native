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
  Close = 'close',
  Error = 'show_error',
}

export enum SdkNotification {
  Pin = 'ONEWELCOME_PIN_NOTIFICATION',
  CustomRegistration = 'ONEWELCOME_CUSTOM_REGISTRATION_NOTIFICATION',
  MobileAuthOtp = 'ONEWELCOME_MOBILE_AUTH_OTP_NOTIFICATION',
  Fingerprint = 'ONEWELCOME_FINGERPRINT_NOTIFICATION',
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
