# Change PIN

## Changing PIN

The Onegini React Native plugin exposes a function (`OneginiSdk.changePin`) to allow the currently logged in user to change their PIN. The user is first required to provide their current PIN, before being allowed to create the new PIN. Both events will be called on class which will extend `OneginiEventListener` and was passed to the plugin with the `Onegini.instance.startApplication()` method.

**Example code to change PIN of currently logged in user:**

```
OneginiSdk.changePin()
.then(() => console.log('Change pin succeed!'))
.catch(error => console.error('Change pin failed: ', error));
```

#TODO: add usePinFlow()
In order to send a pin from a widget use `OneginiPinAuthenticationCallback()` and `OneginiPinRegistrationCallback()`.

Note that the PIN entered by the user should **not** be stored on the device or elsewhere in any shape or form. The Onegini Cordova plugin takes care of this for you in a secure manner.
