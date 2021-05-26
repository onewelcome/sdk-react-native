# changePin

Once authenticated, a user is able to change PIN. This method starts the flow. For further actions please refer to #usePinFlow, #submitPinAction and #addEventListener.

`changePin(): Promise<void>`

**Example**
```
OneginiSdk.changePin()
  .then(() => {
    console.log('Change Pin action succeed!')
  })
  .catch(error => {
    console.log('Change Pin action failed!: ', error.message)
  })
```

**Error**
| Property | Example | Description |
| ------ | ------ |  ----------- |
| code   | 8000   | The error code |
| message   | "Onegini: Internal plugin error"   | Human readable error description |
