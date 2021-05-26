# authenticateUser

Your users can authenticate themselves using any authenticator registered to them. If a user is already authenticated when calling this function, they will be logged out and have to authenticate again.

It may be required to handle authentication with some additional action (PIN/Fingerprint) handling. Please refer to #usePinFlow and #addEventListener

`authenticateUser(profileId: string): Promise<AuthData>`
| Property | Type | Description |
| ------ | ------ | ----------- |
| profileId | string | The profile ID you previously stored during registration |


**Example**
```
OneginiSdk.authenticateUser(profileId)
  .then(authData => {
    console.log('User authentication succeed!')
  })
  .catch(error => {
    console.log('User authentication failed!: ', error.message)
  })
```

**Success**
| Property | Example | Description |
| ------ | ------ |  ----------- |
| AuthData   |  #AuthData  | Authentication data |

**Error**
| Property | Example | Description |
| ------ | ------ |  ----------- |
| code   | 9001   | The error code |
| message   | "Onegini: Configuration error"   | Human readable error description |
