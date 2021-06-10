# Change PIN

The Onegini React Native plugin exposes a function [changePin](../reference-guides/changePin.md) to allow the currently logged in user to change their PIN. The user is first required to provide their current PIN, before being allowed to create the new PIN. Please refer to [usePinFlow](../reference-guides/usePinFlow.md).

**Example code to change PIN of currently logged in user:**

```
OneginiSdk.changePin()
    .then(() => console.log('Change pin succeed!'))
    .catch(error => console.error('Change pin failed: ', error.message));

// ...

const [ flow, pin, visible, isConfirmMode, error, provideNewPinKey, cancelPinFlow] = usePinFlow()
```

Note that the PIN entered by the user should **not** be stored on the device or elsewhere in any shape or form. The Onegini React Native plugin takes care of this for you in a secure manner.
