# usePinFlow

 For easiest PIN flow implementation.

`usePinFlow()`

| Returns | Type | Description |
| ------ | ------ | ----------- |
| flow   | [PinFlow](PinFlow.md)   |  Current state|
| pin   | string   |  Current pin value. |
| visible   | boolean   |  Defines wheather show PIN flow or not. |
| isConfirmMode   | boolean   |  For `create` and `change` user should confirm inserted PIN, this boolean helps to know current state. |
| error   | string | null   | Contains error or empty if no error. |
| provideNewPinKey   | (newKey: string) => void   |  func. Function to supply next PIN char. Supply '<' key to remove last PIN char. |
| cancelPinFlow   | () => void   | Helper function to set error to `null`. |
| pinLength   | number   | The required pin length set by the server |
| userInfo   | string[]  | Array of metadata about the user |


**Example**
```
const [ flow, pin, visible, isConfirmMode, error, provideNewPinKey, cancelPinFlow, pinLength, userInfo] = usePinFlow();
```
