# startClient


The first thing that needs to be done when the app starts is to initizialize the Onegini React Native Plugin. This will perform a few checks and report an error in case of trouble.


`startClient(sdkConfig?: Config): Promise<void>`
| Property | Type | Description |
| ------ | ------ | ----------- |
| sdkConfig | [Config](Config.md) | Config setup that can be passed |

**Example**
```
OneginiSdk.startClient()
    .then(() => console.log('Start succeed'))
    .catch(error => console.log('Start failed: ', error.message))
```

**Error**
| Property | Example | Description |
| ------ | ------ |  ----------- |
| code   | 9001   | The error code |
| message   | "Onegini: Configuration error"   | Human readable error description |
