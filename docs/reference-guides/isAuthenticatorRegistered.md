# isAuthenticatorRegistered

Used to check if an authenticator is registered for the currently authenticated user.

`isAuthenticatorRegistered(profileId: string, type: string): Promise<boolean>`
| Property | Type | Description |
| ------ | ------ | ----------- |
| profileId   | string   | The profile ID you previously stored during registration |
| type   | string   | The authenticator type e.g. "PIN" |

**Example**
```
OneginiSdk.isAuthenticatorRegistered(profileId, 'PIN')
  .then((isRegistered: boolean) => {
    console.log('is Authenticator registered: ', isRegistered)
  })
  .catch(error => {
    console.log('is Authenticator registered failed!: ', error.message)
  })
```


**Success**
| Property | Example | Description |
| ------ | ------ |  ----------- |
| isRegistered   |  true  | true if this authenticator is registered |


**Error**
| Property | Example | Description |
| ------ | ------ |  ----------- |
| code   | 8000   | The error code |
| message   | "Onegini: Internal plugin error"   | Human readable error description |
