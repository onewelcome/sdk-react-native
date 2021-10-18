# submitPinAction

Method which allows to submit actions on PIN flow. 
Please also refer to [usePinFlow](usePinFlow.md).

```
submitPinAction(
    flow: Events.PinFlow,
    action: Events.PinAction,
    pin: string | null
)
```

| Property | Type | Description |
| ------ | ------ | ----------- |
| flow | [PinFlow](PinFlow.md) | Current flow e.g. 'authentication', 'create', 'change' |
| action | [PinAction](PinAction.md) | 'provide', 'cancel' |
| pin | string | Optional PIN |



**Example**
```
OneginiSdk.submitPinAction(
    Events.PinFlow.Change,
    Events.PinAction.Provide,
    '123456'
)
```
