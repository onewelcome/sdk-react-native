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
}

export enum SdkNotification {
  Pin = 'ONEGINI_PIN_NOTIFICATION',
  CustomRegistration = 'ONEGINI_CUSTOM_REGISTRATION_NOTIFICATION',
  MobileAuthOtp = 'ONEGINI_MOBILE_AUTH_OTP_NOTIFICATION',
  Fingerprint = 'ONEGINI_FINGERPRINT_NOTIFICATION',
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

export enum MobileAuthOtpNotification {
  StartAuthentication = 'startAuthentication',
  FinishAuthentication = 'finishAuthentication',
}
