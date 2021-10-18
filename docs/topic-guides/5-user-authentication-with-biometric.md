# User authentication with system biometric authenticators

## Introduction

The Onegini React Native plugin allows you to authenticate users with the system biometric authenticators. These authenticators are provided by the device's operating system (iOS - Touch ID and Face ID, Android - fingerprint) if they are available on the device. System biometric authenticators can be used for both: regular and mobile authentication. Users will be able to retry system biometric authentication as many times as the OS allows them to. If the OS system's biometric authenticators API returns an error for any reason (for example in case of too many failed attempts), the Onegini React Native plugin will revoke system biometric authenticator and will perform a fallback to PIN authentication.

### Requirements

#### FaceID

iOS needs to have configured message displayed on FaceID alert. It's configurable by adding `NSFaceIDUsageDescription` in your `Info.plist` file.

**Example configuration**

    <key>NSFaceIDUsageDescription</key>
    <string>FaceID is used as a authenticator to login to application.</string>

Not specifying this property in your configuration will crash your application when you will try to use Face ID authentication.

### Differences between Android and iOS

It should be noted that there are significant differences between Fingerprint on Android and Touch ID on iOS. As a result, some methods may be available on only one of the operating systems. This will be specified where applicable.

## Enabling system biometric authenticator authentication

In order to enable fingerprint authenticator authentication for a user, the Onegini React Native plugin provides the [registerAuthenticator](../reference-guides/registerAuthenticator.md) to which you need to pass `authenticatorId`. This function requires the user to authenticate.

**Example code for registering the system biometric authenticator:**
```
OneginiSdk
    .registerAuthenticator(profileId, authenticatorId)
    .then(() => {
        console.error("Register authenticator succeed!");
    })
    .catch(error => {
        console.error("Register authenticator failed: " + error);
    });
```

You have to also listen for PIN events in case of fallback. Please refer to [usePinFlow](../reference-guides/usePinFlow.md).

Fingerprint authentication may not be available on every device. In this case, or if the authenticator has already been registered, the above method will return an error.

To request a list of available authenticators, the plugin exposes the [getAllAuthenticators](../reference-guides/getAllAuthenticators.md) function. If the device does not meet the fingerprint requirements, the fingerprint authenticator will not be present in the returned array of of authenticators.

Note that registering a new authenticator does not set it as the preferred authenticator for the user, which is PIN by default. To change this [setPreferredAuthenticator](../reference-guides/setPreferredAuthenticator.md) can be used.

**Example code to set fingerprint as the preferred authenticator:**
```
OneginiSdk.setPreferredAuthenticator(profileId, authenticatorId)
  .then(()) => {
    console.log('setPreferredAuthenticator succeed!')
  })
  .catch(error => {
    console.log('setPreferredAuthenticator failed!: ', error.message)
  })
```
> To set FaceID as the preferred authenticator authenticatorType should be set as "Fingerprint"


## Authenticating a user with fingerprint

Once the fingerprint authenticator has been registered and set as the preferred authenticator, the user is able to authenticate using fingerprint. The method to do so is the same as for PIN, the [authenticateUser](../reference-guides/authenticateUser.md) method.


However, if fingerprint authentication is a possibility for the user, extra handler methods must be implemented. This is in addition to the PIN specific methods (which are necessary in case of fallback to PIN). Please refer to [useFingerprintFlow](../reference-guides/useFingerprintFlow.md).

**Example code to log in a user with fingerprint:**

```
OneginiSdk.registerUser(providerId)
    .then(profile => {
        console.log('Registration success! ', profile);
    })
    .catch(error => {
        console.error('Registration failed: ', error.message);
    })
    
// ...
// Fingerprint handling
// 

const {active, stage, fallbackToPin, cancelFlow} = useFingerprintFlow();
```


If the user fails to authenticate using fingerprint too many times (this limit is set by the OS), the fingerprint authenticator is automatically deregistered and the relevant tokens are revoked by the Onegini React Native plugin. At this point, a fallback to PIN is performed, and the user is request to enter their PIN.
