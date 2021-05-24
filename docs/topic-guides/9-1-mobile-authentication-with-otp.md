# Mobile authentication with OTP

## Introduction

The Onegini Mobile Security Platform offers an ability of mobile authentication with a One Time Password (OTP). Mobile authentication with OTP provides users an easy and secure way for two factor authentication or single factor authentication where no passwords are required. A good use case is for example letting a user login to your web application using his/her mobile device by scanning a QR code displayed within a browser. This essentially allows the user to authenticate using his/her mobile device. It is also not relying on third party services like APNs or FCM. All of the communication stays between App, web application and Mobile Security Platform.

An Example implementation could work like this: A web application fetches the OTP from the Token Server and displays it on the login page in the form of a QR code. Then the user opens your mobile application and scans the QR code with his camera and is automatically logged in into your website. Of course it's up to you to choose how to implement it, the above scenario is just an example.

## How to use

Once you have retrieved an OTP in your application you need to hand it over to the Onegini Flutter Plugin in order to let our SDK process it. Use `OneginiSdk.handleMobileAuthWithOtp()` for passing OTP is SDK.

```
OneginiSdk.handleMobileAuthWithOtp(otpCode)
.then(() => {
  console.log('Authentication successful')
})
.catch((error) => {
  console.error('Authentication failed: `, error)
})
```

**TODO**: handle events `ONEGINI_MOBILE_AUTH_OTP_NOTIFICATION`
