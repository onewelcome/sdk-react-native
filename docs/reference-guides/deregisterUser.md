# deregisterUser

Deregistration is the process of removing a user (profile) from the device and server.

`deregisterUser(profileId: string): Promise<void>`
| Property | Type | Description |
| ------ | ------ | ----------- |
| profileId | string | The profile ID you previously stored during registration |

**Example**
```
OneginiSdk.deregisterUser(profileId)
    .then(() => console.log('Deregister succeed!'))
    .catch(error => console.error('Deregister failed: ', error.message))
```

**Error**
| Property | Example | Description |
| ------ | ------ |  ----------- |
| code   | 9001   | The error code |
| message   | "Onegini: Configuration error"   | Human readable error description |
