
# submitPinAction

use can use #usePinFlow here

```
submitPinAction(
    flow: Events.PinFlow,
    action: Events.PinAction,
    pin: string | null
)
```

| Property | Description |
| ------ | ----------- |
| flow   | --- |
| action   | --- |
| pin   | --- |

**Example**
```
OneginiSdk.submitPinAction(
    Events.PinFlow.Change,
    Events.PinAction.Cancel,
    null
)
```
