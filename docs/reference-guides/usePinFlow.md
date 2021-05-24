
# usePinFlow

 For easiest PIN flow implementation.

`usePinFlow()`

| Returns | Type | Description |
| ------ | ------ | ----------- |
| flow   | Events.PinFlow   |  Current state|
| pin   | string   |  Current pin value. |
| visible   | boolean   |  Defines wheather show PIN flow or not. |
| isConfirmMode   | boolean   |  For `create` and `change` user should confirm inserted PIN, this boolean helps to know current state. |
| error   | string \| null   | Contains error or empty if no error. |
| provideNewPinKey   | (newKey: string) => void   |  func. Function to supply next PIN char. Supply '<' key to remove last PIN char. |
| cancelPinFlow   | () => void   | Helper function to set error to `null`. |

**Example**
```
const [ flow, pin, visible, isConfirmMode, error, provideNewPinKey, cancelPinFlow] = usePinFlow();
```
