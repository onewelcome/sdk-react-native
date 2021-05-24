
# startClient


The first thing that needs to be done when the app starts is to initizialize the Onegini React Native Plugin. This will perform a few checks and report an error in case of trouble.


`startClient(sdkConfig?: Types.Config): Promise<void>`
| Property | Description |
| ------ | ----------- |
| sdkConfig   | Config setup that can be passed (see #Config). |

**Example**
```
OneginiSdk.startClient()
    .then(() => console.log('Start succeed'))
    .catch(err => console.log('Start failed: ', err))
```
